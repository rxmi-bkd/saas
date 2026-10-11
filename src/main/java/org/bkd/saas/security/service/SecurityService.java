package org.bkd.saas.security.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bkd.saas.security.exception.InvalidTokenException;
import org.bkd.saas.user.dto.UserWithPasswordDto;
import org.bkd.saas.user.exception.UserAlreadyEnabledException;
import org.bkd.saas.user.exception.UserNotFoundException;
import org.bkd.saas.user.service.UserEmailService;
import org.bkd.saas.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SecurityService {
  private final UserService userService;
  private final PasswordResetTokenService passwordResetTokenService;
  private final UserEmailService userEmailService;

  public void sendActivationEmail(String email) {
    UserWithPasswordDto user =
        userService
            .readOptionalUserWithPassword(email)
            .orElseThrow(() -> new UserNotFoundException(email));

    if (user.enabled()) {
      throw new UserAlreadyEnabledException(email);
    }

    userEmailService.sendActivationEmail(user.id(), user.email());
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
