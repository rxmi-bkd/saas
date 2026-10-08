package org.bkd.saas.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bkd.saas.SharedAssertions.assertNoContent;
import static org.bkd.saas.security.SecurityAssertions.assertAccessToken;

import org.bkd.saas.AbstractIntegrationTests;
import org.bkd.saas.security.rest.request.ActivateAccountRequest;
import org.bkd.saas.security.rest.request.LoginRequest;
import org.bkd.saas.security.service.ActivationTokenService;
import org.bkd.saas.user.db.AppUserEntity;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class ActivateAccountTests extends AbstractIntegrationTests {
  private static final String EMAIL = "test@test.com";
  private static final String PASSWORD = "test";

  @Autowired private UserRepository userRepository;
  @Autowired private ActivationTokenService activationTokenService;

  @BeforeEach
  void beforeEach() {
    userRepository.deleteAll();
  }

  @Test
  void activateAccount_enablesUser() {
    // arrange
    userClient.createUserOk(new CreateUserRequest(EMAIL, PASSWORD));
    AppUserEntity user = userRepository.findByEmail(EMAIL).orElseThrow();
    assertThat(user.isEnabled()).isFalse();
    String jwt = activationTokenService.createJwt(user.getId());

    // act
    ResponseEntity<Void> response =
        securityClient.activateAccountOk(new ActivateAccountRequest(jwt));

    // assert
    assertNoContent(response);
    assertThat(userRepository.findByEmail(EMAIL).orElseThrow().isEnabled()).isTrue();
    assertAccessToken(securityClient.loginOk(new LoginRequest(EMAIL, PASSWORD)));
  }

  @Test
  void activateAccount_withInvalidToken_returnsUnauthorized() {
    // act
    var response = securityClient.activateAccountKo(new ActivateAccountRequest("invalid"));

    // assert
    assertThat(response.getStatusCode().value()).isEqualTo(401);
  }
}
