package org.bkd.saas.security.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bkd.saas.security.dto.RefreshTokenDto;
import org.bkd.saas.security.dto.TokenPairDto;
import org.bkd.saas.security.exception.InvalidCredentialsException;
import org.bkd.saas.security.exception.TokenException;
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

  public TokenPairDto login(String email, String password) {
    Optional<UserWithPasswordDto> user = userService.readOptionalUserWithPassword(email);

    if (user.isEmpty()) {
      throw new InvalidCredentialsException();
    }

    boolean isPasswordCorrect = passwordEncoder.matches(password, user.get().password());

    if (!isPasswordCorrect || !user.get().enabled()) {
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
    }
  }

  @Transactional(noRollbackFor = TokenException.class)
  public TokenPairDto refresh(String token) {
    RefreshTokenDto tokenDto = refreshTokenService.readToken(token);

    if (refreshTokenService.isRevoked(tokenDto)) {
      // A revoked token is being replayed: assume theft and kill the whole family.
      refreshTokenService.revokeTokenFamily(tokenDto.familyId());
      throw new TokenException();
    }

    if (refreshTokenService.isExpired(tokenDto)) {
      throw new TokenException();
    }

    Optional<UserDto> user = userService.readOptionalUser(tokenDto.userId());

    if (user.isEmpty()) {
      refreshTokenService.revokeTokenFamily(tokenDto.familyId());
      throw new TokenException();
    }

    if (!user.get().enabled()) {
      refreshTokenService.revokeTokenFamily(tokenDto.familyId());
      throw new TokenException();
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
