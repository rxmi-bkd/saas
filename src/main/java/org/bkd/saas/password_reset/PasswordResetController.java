package org.bkd.saas.password_reset;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.password_reset.requests.ForgotPasswordRequest;
import org.bkd.saas.password_reset.requests.ResetPasswordRequest;
import org.bkd.saas.password_reset.services.PasswordResetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PasswordResetController {
  private final PasswordResetService passwordResetService;

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
