package org.bkd.saas.authentication;

import static org.bkd.saas.shared.Constants.PUBLIC_BASE_PATH;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.authentication.services.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  public static final String PUBLIC_ENDPOINT = PUBLIC_BASE_PATH + "/authentication";

  @PostMapping(PUBLIC_ENDPOINT + "/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return authenticationService.login(request.email(), request.password());
  }
}
