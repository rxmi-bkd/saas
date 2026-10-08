package org.bkd.saas.security.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bkd.saas.security.dto.AccessTokenDto;
import org.bkd.saas.security.exception.InvalidCredentialsException;
import org.bkd.saas.security.exception.InvalidTokenException;
import org.bkd.saas.user.dto.UserWithPasswordDto;
import org.bkd.saas.user.exception.UserNotFoundException;
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

  public AccessTokenDto login(String email, String password) {
    UserWithPasswordDto user =
        userService
            .readOptionalUserWithPassword(email)
            .orElseThrow(() -> new UserNotFoundException(email));

    boolean isPasswordCorrect = passwordEncoder.matches(password, user.password());

    if (!isPasswordCorrect) {
      throw new InvalidCredentialsException();
    }

    String accessToken = accessTokenService.createJwt(user.id(), user.role(), user.enabled());
    return new AccessTokenDto(accessToken);
  }

  public void forgotPassword(String email) {
    UserWithPasswordDto user =
        userService
            .readOptionalUserWithPassword(email)
            .orElseThrow(() -> new UserNotFoundException(email));

    String jwt = passwordResetTokenService.createJwt(user.id(), user.password());

    log.info("jwt = {}", jwt);
  }

  public void resetPassword(String jwt, String newPassword) {
    boolean isValidJwt = passwordResetTokenService.isValidJwt(jwt);

    if (isValidJwt) {
      UUID userId = passwordResetTokenService.readSubject(jwt);
      userService.updateUserPassword(userId, newPassword);
      return;
    }

    throw new InvalidTokenException(jwt);
  }
}
