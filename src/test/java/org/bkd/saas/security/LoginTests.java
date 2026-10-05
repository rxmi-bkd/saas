package org.bkd.saas.security;

import static org.bkd.saas.SharedAssertions.assertError;
import static org.bkd.saas.security.SecurityAssertions.assertTokenPair;

import org.bkd.saas.AbstractIntegrationTests;
import org.bkd.saas.security.dto.TokenPairDto;
import org.bkd.saas.security.rest.request.LoginRequest;
import org.bkd.saas.shared.dto.ErrorDto;
import org.bkd.saas.user.db.AppUserEntity;
import org.bkd.saas.user.db.UserRepository;
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
  void login_returnsTokenPair() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    users.createUserOk(createUserRequest);
    ResponseEntity<TokenPairDto> response = security.loginOk(loginRequest);

    // assert
    assertTokenPair(response);
  }

  @Test
  void login_withUnknownEmail_returnsUnauthorized() {
    // arrange
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    ResponseEntity<ErrorDto> response = security.loginKo(loginRequest);

    // assert
    assertError(response, 401, "Unauthorized", "Invalid email or password");
  }

  @Test
  void login_withWrongPassword_returnsUnauthorized() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, "wrong-password");

    // act
    users.createUserOk(createUserRequest);
    ResponseEntity<ErrorDto> response = security.loginKo(loginRequest);

    // assert
    assertError(response, 401, "Unauthorized", "Invalid email or password");
  }

  @Test
  void login_withDisabledUser_returnsUnauthorized() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    users.createUserOk(createUserRequest);
    disableUser(EMAIL);
    ResponseEntity<ErrorDto> response = security.loginKo(loginRequest);

    // assert
    assertError(response, 401, "Unauthorized", "Invalid email or password");
  }

  private void disableUser(String email) {
    AppUserEntity user = userRepository.findByEmail(email).orElseThrow();
    user.setEnabled(false);
    userRepository.save(user);
  }
}
