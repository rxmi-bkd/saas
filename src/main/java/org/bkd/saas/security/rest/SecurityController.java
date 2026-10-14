package org.bkd.saas.security.rest;

import static org.bkd.saas.security.rest.Routes.FORGOT_PASSWORD;
import static org.bkd.saas.security.rest.Routes.LOGIN;
import static org.bkd.saas.security.rest.Routes.SEND_ACTIVATION_EMAIL;
import static org.bkd.saas.security.rest.Routes.RESET_PASSWORD;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.security.rest.request.ForgotPasswordRequest;
import org.bkd.saas.security.rest.request.LoginRequest;
import org.bkd.saas.security.rest.request.PasswordResetRequest;
import org.bkd.saas.security.rest.request.SendActivationEmailRequest;
import org.bkd.saas.security.service.SecurityService;
import org.bkd.saas.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SecurityController {
  private final SecurityService securityService;

  @PostMapping(LOGIN)
  public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequest request_,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
    UserDto user = securityService.login(request_.email(), request_.password(), request, response);
    return ResponseEntity.ok().body(user);
  }

  @PostMapping(SEND_ACTIVATION_EMAIL)
  public ResponseEntity<Void> sendActivationEmail(@Valid @RequestBody SendActivationEmailRequest request) {
    securityService.sendActivationEmail(request.email());
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
