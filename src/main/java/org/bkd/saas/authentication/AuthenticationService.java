package org.bkd.saas.authentication;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.jwt.JwtService;
import org.bkd.saas.user.CredentialsService;
import org.bkd.saas.user.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

  private final JwtService jwtService;
  private final CredentialsService credentialsService;

  public LoginResponse login(String email, String password) {
    Optional<UserResponse> user = credentialsService.verifyCredentials(email, password);
    if (user.isEmpty()) throw new InvalidCredentialsException();
    String accessToken = jwtService.createJwt(user.get().id(), user.get().role());
    return new LoginResponse(accessToken);
  }
}
