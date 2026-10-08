package org.bkd.saas.security;

import static org.bkd.saas.SharedAssertions.assertError;
import static org.bkd.saas.security.SecurityAssertions.assertAccessToken;

import org.bkd.saas.AbstractIntegrationTests;
import org.bkd.saas.security.dto.AccessTokenDto;
import org.bkd.saas.security.exception.InvalidCredentialsException;
import org.bkd.saas.security.rest.request.LoginRequest;
import org.bkd.saas.shared.dto.ErrorDto;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.exception.DisabledUserException;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class LoginTests extends AbstractIntegrationTests {
  private static final String EMAIL = "test@test.com";
  private static final String PASSWORD = "test";

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void beforeEach() {
    userRepository.deleteAll();
  }

  @Test
  void login_returnsAccessToken() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    userClient.createUserOk(createUserRequest);
    enableAllUsers();
    ResponseEntity<AccessTokenDto> response = securityClient.loginOk(loginRequest);

    // assert
    assertAccessToken(response);
  }

  @Test
  void login_withUnknownEmail_returnsUnauthorized() {
    // arrange
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    ResponseEntity<ErrorDto> response = securityClient.loginKo(loginRequest);

    // assert
    assertError(response, 401, "Unauthorized", InvalidCredentialsException.ERROR_MSG);
  }

  @Test
  void login_withWrongPassword_returnsUnauthorized() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, "wrong-password");

    // act
    userClient.createUserOk(createUserRequest);
    ResponseEntity<ErrorDto> response = securityClient.loginKo(loginRequest);

    // assert
    assertError(response, 401, "Unauthorized", InvalidCredentialsException.ERROR_MSG);
  }

  @Test
  void login_withDisabledUser_returnsForbidden() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    userClient.createUserOk(createUserRequest);
    ResponseEntity<ErrorDto> response = securityClient.loginKo(loginRequest);

    // assert
    assertError(response, 403, "Forbidden", DisabledUserException.ERROR_MSG);
  }

  private void enableAllUsers() {
    userRepository
        .findAll()
        .forEach(
            user -> {
              user.setEnabled(true);
              userRepository.save(user);
            });
  }
}
