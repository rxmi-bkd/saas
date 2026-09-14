package org.bkd.saas.authentication.services;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.authentication.InvalidCredentialsException;
import org.bkd.saas.authentication.LoginResponse;
import org.bkd.saas.user.responses.UserResponse;
import org.bkd.saas.user.services.CredentialsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

  private final AuthenticationTokenService authenticationTokenService;
  private final CredentialsService credentialsService;

  public LoginResponse login(String email, String password) {
    Optional<UserResponse> user = credentialsService.verifyCredentials(email, password);
    if (user.isEmpty()) throw new InvalidCredentialsException();
    String accessToken = authenticationTokenService.createJwt(user.get().id(), user.get().role());
    return new LoginResponse(accessToken);
  }
}
