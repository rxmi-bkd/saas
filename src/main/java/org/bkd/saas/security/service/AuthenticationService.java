package org.bkd.saas.security.service;

import static org.bkd.saas.shared.SecurityUtils.SECURE_RANDOM;
import static org.bkd.saas.shared.SecurityUtils.encodeToBase64;

import jakarta.annotation.PostConstruct;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bkd.saas.security.dto.RefreshTokenDto;
import org.bkd.saas.security.dto.TokenPairDto;
import org.bkd.saas.security.exception.InvalidCredentialsException;
import org.bkd.saas.security.exception.InvalidTokenException;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.dto.UserWithPasswordDto;
import org.bkd.saas.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final AccessTokenService accessTokenService;
  private final RefreshTokenService refreshTokenService;
  private final PasswordResetTokenService passwordResetTokenService;

  private String dummyPasswordHash;

  @PostConstruct
  void postConstruct() {
    byte[] randomBytes = new byte[32];
    SECURE_RANDOM.nextBytes(randomBytes);
    dummyPasswordHash = passwordEncoder.encode(encodeToBase64(randomBytes));
  }

  public TokenPairDto login(String email, String password) {
    Optional<UserWithPasswordDto> user = userService.readOptionalUserWithPassword(email);

    // Always run BCrypt, even for unknown emails,
    // so response time does not reveal whether an account exists.
    String hashToCheck = user.map(UserWithPasswordDto::password).orElse(dummyPasswordHash);
    boolean isPasswordCorrect = passwordEncoder.matches(password, hashToCheck);

    if (user.isEmpty() || !isPasswordCorrect || !user.get().enabled()) {
      throw new InvalidCredentialsException();
    }

    String accessToken = accessTokenService.createJwt(user.get().id(), user.get().role());
    String refreshToken = refreshTokenService.createToken(user.get().id());
    return new TokenPairDto(accessToken, refreshToken);
  }

  public void forgotPassword(String email) {
    Optional<UserWithPasswordDto> user = userService.readOptionalUserWithPassword(email);

    if (user.isEmpty()) {
      return;
    }

    String jwt = passwordResetTokenService.createJwt(user.get().id(), user.get().password());
    log.info("jwt = {}", jwt);
  }

  public void resetPassword(String jwt, String newPassword) {
    UUID userId = passwordResetTokenService.readSubject(jwt);
    boolean isValidJwt = passwordResetTokenService.isValidJwt(jwt);

    if (isValidJwt) {
      userService.updateUserPassword(userId, newPassword);
      refreshTokenService.revokeUserTokens(userId);
      return;
    }

    throw new InvalidTokenException();
  }

  @Transactional(noRollbackFor = InvalidTokenException.class)
  public TokenPairDto refresh(String token) {
    RefreshTokenDto tokenDto =
        refreshTokenService.readOptionalToken(token).orElseThrow(InvalidTokenException::new);

    if (refreshTokenService.isExpiredToken(tokenDto)) {
      throw new InvalidTokenException();
    }

    if (refreshTokenService.isRevokedToken(tokenDto)) {
      refreshTokenService.revokeTokenFamily(tokenDto.familyId());
      throw new InvalidTokenException();
    }

    Optional<UserDto> user = userService.readOptionalUser(tokenDto.userId());

    if (user.isEmpty()) {
      refreshTokenService.revokeTokenFamily(tokenDto.familyId());
      throw new InvalidTokenException();
    }

    if (!user.get().enabled()) {
      refreshTokenService.revokeTokenFamily(tokenDto.familyId());
      throw new InvalidTokenException();
    }

    refreshTokenService.revokeToken(tokenDto.id());

    String access = accessTokenService.createJwt(user.get().id(), user.get().role());
    String refresh = refreshTokenService.createToken(tokenDto.userId(), tokenDto.familyId());
    return new TokenPairDto(access, refresh);
  }

  public void logout(String token) {
    refreshTokenService.revokeTokenFamily(token);
  }
}
