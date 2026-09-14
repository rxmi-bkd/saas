package org.bkd.saas.password_reset.services;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bkd.saas.user.exceptions.UserNotFoundException;
import org.bkd.saas.user.responses.UserWithPasswordResponse;
import org.bkd.saas.user.services.UserPasswordService;
import org.bkd.saas.user.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PasswordResetService {

  private final UserService userService;
  private final UserPasswordService userPasswordService;
  private final PasswordResetTokenService passwordResetTokenService;

  public void forgotPassword(String email) {
    try {
      UserWithPasswordResponse user = userService.readUserWithPassword(email);
      String jwt = passwordResetTokenService.createJwt(user.id(), user.password());
      log.info("Password reset requested for {} : {}", email, jwt);
    } catch (UserNotFoundException e) {
      log.info("User with email {} not found", email);
    }
  }

  public void resetPassword(String jwt, String newPassword) {
    Claims claims = passwordResetTokenService.readJwt(jwt);
    UUID userId = UUID.fromString(claims.getSubject());
    UserWithPasswordResponse user = userService.readUserWithPassword(userId);
    if (passwordResetTokenService.isValidJwt(jwt, user.password())) {
      userPasswordService.updateUserPassword(userId, newPassword);
    }
  }
}
