package org.bkd.saas.security.rest;

import static org.bkd.saas.security.rest.Routes.ACTIVATE_ACCOUNT;
import static org.bkd.saas.security.rest.Routes.FORGOT_PASSWORD;
import static org.bkd.saas.security.rest.Routes.LOGIN;
import static org.bkd.saas.security.rest.Routes.RESET_PASSWORD;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.security.dto.AccessTokenDto;
import org.bkd.saas.security.rest.request.ActivateAccountRequest;
import org.bkd.saas.security.rest.request.ForgotPasswordRequest;
import org.bkd.saas.security.rest.request.LoginRequest;
import org.bkd.saas.security.rest.request.PasswordResetRequest;
import org.bkd.saas.security.service.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SecurityController {
  private final SecurityService securityService;

  @PostMapping(LOGIN)
  public ResponseEntity<AccessTokenDto> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(securityService.login(request.email(), request.password()));
  }

  @PostMapping(ACTIVATE_ACCOUNT)
  public ResponseEntity<Void> activateAccount(@Valid @RequestBody ActivateAccountRequest request) {
    securityService.activateAccount(request.jwt());
    return ResponseEntity.noContent().build();
  }

  @PostMapping(FORGOT_PASSWORD)
  public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    securityService.forgotPassword(request.email());
    return ResponseEntity.noContent().build();
  }

  @PostMapping(RESET_PASSWORD)
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
    securityService.resetPassword(request.jwt(), request.newPassword());
    return ResponseEntity.noContent().build();
  }
}
