package org.bkd.saas.password_reset.rest;

import static org.bkd.saas.password_reset.rest.Routes.FORGOT_PASSWORD;
import static org.bkd.saas.password_reset.rest.Routes.RESET_PASSWORD;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.password_reset.rest.request.ForgotPasswordRequest;
import org.bkd.saas.password_reset.rest.request.PasswordResetRequest;
import org.bkd.saas.password_reset.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PasswordResetController {
  private final PasswordResetService passwordResetService;

  @PostMapping(FORGOT_PASSWORD)
  public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    passwordResetService.forgotPassword(request.email());
    return ResponseEntity.noContent().build();
  }

  @PostMapping(RESET_PASSWORD)
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
    passwordResetService.resetPassword(request.jwt(), request.newPassword());
    return ResponseEntity.noContent().build();
  }
}
