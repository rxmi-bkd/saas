package org.bkd.saas.authentication.rest;

import static org.bkd.saas.authentication.rest.Routes.FORGOT_PASSWORD;
import static org.bkd.saas.authentication.rest.Routes.LOGIN;
import static org.bkd.saas.authentication.rest.Routes.LOGOUT;
import static org.bkd.saas.authentication.rest.Routes.REFRESH;
import static org.bkd.saas.authentication.rest.Routes.RESET_PASSWORD;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.authentication.dto.TokenPairDto;
import org.bkd.saas.authentication.rest.request.ForgotPasswordRequest;
import org.bkd.saas.authentication.rest.request.LoginRequest;
import org.bkd.saas.authentication.rest.request.LogoutRequest;
import org.bkd.saas.authentication.rest.request.PasswordResetRequest;
import org.bkd.saas.authentication.rest.request.RefreshTokenRequest;
import org.bkd.saas.authentication.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  @PostMapping(LOGIN)
  public ResponseEntity<TokenPairDto> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authenticationService.login(request.email(), request.password()));
  }

  @PostMapping(REFRESH)
  public ResponseEntity<TokenPairDto> refresh(@Valid @RequestBody RefreshTokenRequest request) {
    return ResponseEntity.ok(authenticationService.refresh(request.refresh()));
  }

  @PostMapping(LOGOUT)
  public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
    authenticationService.logout(request.refresh());
    return ResponseEntity.noContent().build();
  }

  @PostMapping(FORGOT_PASSWORD)
  public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    authenticationService.forgotPassword(request.email());
    return ResponseEntity.noContent().build();
  }

  @PostMapping(RESET_PASSWORD)
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
    authenticationService.resetPassword(request.jwt(), request.newPassword());
    return ResponseEntity.noContent().build();
  }
}
