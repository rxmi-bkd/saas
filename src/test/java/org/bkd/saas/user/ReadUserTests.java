package org.bkd.saas.user;

import static org.bkd.saas.SharedAssertions.assertStatus;
import static org.bkd.saas.user.UserAssertions.assertUserRead;

import org.bkd.saas.AbstractIntegrationTests;
import org.bkd.saas.security.rest.request.LoginRequest;
import org.bkd.saas.shared.dto.ErrorDto;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class ReadUserTests extends AbstractIntegrationTests {
  private static final String EMAIL = "test@test.com";
  private static final String PASSWORD = "test";

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void beforeEach() {
    userRepository.deleteAll();
  }

  @Test
  void me_returnsAuthenticatedUser() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    UserDto user = userClient.createUserOk(createUserRequest).getBody();
    enableUser(EMAIL);
    String accessToken = securityClient.loginOk(loginRequest).getBody().access();
    ResponseEntity<UserDto> response = userClient.meOk(accessToken);

    // assert
    assertUserRead(response, user.id(), EMAIL);
  }

  @Test
  void me_withInvalidToken_returnsForbidden() {
    // arrange
    String accessToken = "invalid-token";

    // act
    ResponseEntity<ErrorDto> response = userClient.meKo(accessToken);

    // assert
    assertStatus(response, 403);
  }
}
