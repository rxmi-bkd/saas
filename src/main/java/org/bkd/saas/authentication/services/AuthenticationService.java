package org.bkd.saas.authentication.services;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.authentication.InvalidCredentialsException;
import org.bkd.saas.authentication.LoginResponse;
import org.bkd.saas.user.services.CredentialsService;
import org.bkd.saas.user.requests.responses.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

  private final AccessJwtService accessJwtService;
  private final CredentialsService credentialsService;

  public LoginResponse login(String email, String password) {
    Optional<UserResponse> user = credentialsService.verifyCredentials(email, password);
    if (user.isEmpty()) throw new InvalidCredentialsException();
    String accessToken = accessJwtService.createJwt(user.get().id(), user.get().role());
    return new LoginResponse(accessToken);
  }
}
