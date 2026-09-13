package org.bkd.saas.authentication;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.authentication.services.AuthenticationService;
import org.bkd.saas.reset_password.requests.ForgotPasswordRequest;
import org.bkd.saas.reset_password.services.PasswordResetService;
import org.bkd.saas.reset_password.requests.ResetPasswordRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;
  private final PasswordResetService passwordResetService;

  @PostMapping("/api/public/authentication/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return authenticationService.login(request.email(), request.password());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PostMapping("/api/public/authentication/forgot-password")
  public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    passwordResetService.forgotPassword(request.email());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PostMapping("/api/public/authentication/reset-password")
  public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    passwordResetService.resetPassword(request.jwt(), request.newPassword());
  }
}
