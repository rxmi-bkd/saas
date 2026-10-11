package org.bkd.saas.security.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bkd.saas.security.exception.InvalidCredentialsException;
import org.bkd.saas.security.exception.InvalidTokenException;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.dto.UserWithPasswordDto;
import org.bkd.saas.user.exception.DisabledUserException;
import org.bkd.saas.user.exception.UserAlreadyEnabledException;
import org.bkd.saas.user.exception.UserNotFoundException;
import org.bkd.saas.user.service.EmailService;
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
  private final PasswordResetTokenService passwordResetTokenService;
  private final ActivationTokenService activationTokenService;
  private final EmailService emailService;

  public UserDto login(String email, String password) {
    UserWithPasswordDto user =
        userService
            .readOptionalUserWithPassword(email)
            .orElseThrow(InvalidCredentialsException::new);

    if (!passwordEncoder.matches(password, user.password())) {
      throw new InvalidCredentialsException();
    }

    if (!user.enabled()) {
      throw new DisabledUserException(user.email());
    }

    return userService.readUser(user.id());
  }

  public void activateAccount(String jwt) {
    UUID userId = activationTokenService.readSubject(jwt);
    userService.enableUser(userId);
  }

  public void resendActivationEmail(String email) {
    UserWithPasswordDto user =
        userService
            .readOptionalUserWithPassword(email)
            .orElseThrow(() -> new UserNotFoundException(email));

    if (user.enabled()) {
      throw new UserAlreadyEnabledException(email);
    }

    emailService.sendActivationEmail(user.id(), user.email());
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
