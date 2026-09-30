package org.bkd.saas.authentication.rest;

import static org.bkd.saas.authentication.rest.Routes.LOGIN;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.authentication.dto.AuthenticationTokenDto;
import org.bkd.saas.authentication.rest.request.LoginRequest;
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
  public ResponseEntity<AuthenticationTokenDto> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authenticationService.login(request.email(), request.password()));
  }
}
