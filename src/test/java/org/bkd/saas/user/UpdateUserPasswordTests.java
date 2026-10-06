package org.bkd.saas.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bkd.saas.SharedAssertions.assertError;
import static org.bkd.saas.SharedAssertions.assertNoContent;

import org.bkd.saas.AbstractIntegrationTests;
import org.bkd.saas.security.rest.request.LoginRequest;
import org.bkd.saas.shared.dto.ErrorDto;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.bkd.saas.user.rest.request.UpdateUserPasswordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UpdateUserPasswordTests extends AbstractIntegrationTests {
  private static final String EMAIL = "test@test.com";
  private static final String PASSWORD = "test";
  private static final String NEW_PASSWORD = "new-password";
  private static final String WRONG_PASSWORD = "wrong";

  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void beforeEach() {
    userRepository.deleteAll();
  }

  @Test
  void updateUserPassword_returnsNoContentAndPersistsNewPassword() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
    UpdateUserPasswordRequest updateRequest = new UpdateUserPasswordRequest(PASSWORD, NEW_PASSWORD);

    // act
    UserDto user = userClient.createUserOk(createUserRequest).getBody();
    String accessToken = securityClient.loginOk(loginRequest).getBody().access();
    ResponseEntity<Void> response = userClient.updateUserPasswordOk(updateRequest, accessToken);

    // assert
    assertNoContent(response);
    String persistedPassword = userRepository.findById(user.id()).orElseThrow().getPassword();
    assertThat(passwordEncoder.matches(NEW_PASSWORD, persistedPassword)).isTrue();
  }

  @Test
  void updateUserPassword_withIncorrectOldPassword_returnsBadRequest() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
    UpdateUserPasswordRequest updateRequest =
        new UpdateUserPasswordRequest(WRONG_PASSWORD, NEW_PASSWORD);

    // act
    UserDto user = userClient.createUserOk(createUserRequest).getBody();
    String accessToken = securityClient.loginOk(loginRequest).getBody().access();
    ResponseEntity<ErrorDto> response = userClient.updateUserPasswordKo(updateRequest, accessToken);

    // assert
    assertError(response, 400, "Bad Request", "Old password is incorrect");
    String persistedPassword = userRepository.findById(user.id()).orElseThrow().getPassword();
    assertThat(passwordEncoder.matches(PASSWORD, persistedPassword)).isTrue();
  }
}
