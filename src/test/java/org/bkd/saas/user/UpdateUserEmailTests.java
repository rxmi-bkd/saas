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
import org.bkd.saas.user.rest.request.UpdateUserEmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class UpdateUserEmailTests extends AbstractIntegrationTests {
  private static final String EMAIL = "test@test.com";
  private static final String OTHER_EMAIL = "other@test.com";
  private static final String NEW_EMAIL = "new@test.com";
  private static final String PASSWORD = "test";

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void beforeEach() {
    userRepository.deleteAll();
  }

  @Test
  void updateUserEmail_returnsNoContentAndPersistsNewEmail() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
    UpdateUserEmailRequest updateRequest = new UpdateUserEmailRequest(NEW_EMAIL);

    // act
    UserDto user = userClient.createUserOk(createUserRequest).getBody();
    String accessToken = securityClient.loginOk(loginRequest).getBody().access();
    ResponseEntity<Void> response = userClient.updateUserEmailOk(updateRequest, accessToken);

    // assert
    assertNoContent(response);
    assertThat(userRepository.findById(user.id()).orElseThrow().getEmail()).isEqualTo(NEW_EMAIL);
  }

  @Test
  void updateUserEmail_withEmailUsedByAnotherUser_returnsConflict() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    CreateUserRequest otherUserRequest = new CreateUserRequest(OTHER_EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
    UpdateUserEmailRequest updateRequest = new UpdateUserEmailRequest(OTHER_EMAIL);

    // act
    UserDto user = userClient.createUserOk(createUserRequest).getBody();
    userClient.createUserOk(otherUserRequest);
    String accessToken = securityClient.loginOk(loginRequest).getBody().access();
    ResponseEntity<ErrorDto> response = userClient.updateUserEmailKo(updateRequest, accessToken);

    // assert
    assertError(response, 409, "Conflict", "Email already used");
    assertThat(userRepository.findById(user.id()).orElseThrow().getEmail()).isEqualTo(EMAIL);
  }
}
