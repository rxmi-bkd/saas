package org.bkd.saas.password_reset.services;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bkd.saas.user.exceptions.UserNotFoundException;
import org.bkd.saas.user.responses.UserWithPasswordResponse;
import org.bkd.saas.user.services.PasswordService;
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
  private final PasswordService passwordService;
  private final PasswordResetJwtService passwordResetJwtService;

  public void forgotPassword(String email) {
    try {
      UserWithPasswordResponse user = userService.readUserWithPassword(email);
      String jwt = passwordResetJwtService.createJwt(user.id(), user.password());
      log.info("Password reset requested for {} : {}", email, jwt);
    } catch (UserNotFoundException e) {
      log.info("User with email {} not found", email);
    }
  }

  public void resetPassword(String jwt, String newPassword) {
    Claims claims = passwordResetJwtService.readJwt(jwt);
    UUID userId = UUID.fromString(claims.getSubject());
    UserWithPasswordResponse user = userService.readUserWithPassword(userId);
    if (passwordResetJwtService.isValidJwt(jwt, user.password())) {
      passwordService.updateUserPassword(userId, newPassword);
    }
  }
}
