package org.bkd.saas.security.service;

import static io.jsonwebtoken.Claims.SUBJECT;
import static org.bkd.saas.shared.SecurityUtils.randomToken;

import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bkd.saas.security.dto.AccessTokenDto;
import org.bkd.saas.security.exception.InvalidCredentialsException;
import org.bkd.saas.security.exception.InvalidTokenException;
import org.bkd.saas.user.dto.UserWithPasswordDto;
import org.bkd.saas.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SecurityService {
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final AccessTokenService accessTokenService;
  private final PasswordResetTokenService passwordResetTokenService;

  private String dummyPasswordHash;

  @PostConstruct
  public void postConstruct() {
    dummyPasswordHash = passwordEncoder.encode(randomToken());
  }

  public AccessTokenDto login(String email, String password) {
    Optional<UserWithPasswordDto> user = userService.readOptionalUserWithPassword(email);

    // Always run "passwordEncoder.matches", even for unknown emails,
    // so response time does not reveal whether an account exists.
    String hashToCheck = user.map(UserWithPasswordDto::password).orElse(dummyPasswordHash);
    boolean isPasswordCorrect = passwordEncoder.matches(password, hashToCheck);

    if (!isPasswordCorrect) {
      throw new InvalidCredentialsException();
    }

    String accessToken = accessTokenService.createJwt(user.get().id(), user.get().role());
    return new AccessTokenDto(accessToken);
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
    boolean isValidJwt = passwordResetTokenService.isValidJwt(jwt);

    if (isValidJwt) {
      Claims claims = passwordResetTokenService.readJwt(jwt);
      UUID userId = claims.get(SUBJECT, UUID.class);
      userService.updateUserPassword(userId, newPassword);
      return;
    }

    throw new InvalidTokenException(jwt);
  }
}
