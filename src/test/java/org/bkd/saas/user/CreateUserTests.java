package org.bkd.saas.user;

import static org.bkd.saas.SharedAssertions.assertError;
import static org.bkd.saas.user.UserAssertions.assertUserCreated;

import org.bkd.saas.AbstractIntegrationTests;
import org.bkd.saas.shared.dto.ErrorDto;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class CreateUserTests extends AbstractIntegrationTests {
  private static final String EMAIL = "test@test.com";
  private static final String PASSWORD = "test";

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void beforeEach() {
    userRepository.deleteAll();
  }

  @Test
  void createUser_returnsCreatedUser() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);

    // act
    ResponseEntity<UserDto> response = users.createUserOk(createUserRequest);

    // assert
    assertUserCreated(response, EMAIL);
  }

  @Test
  void createUser_withAlreadyUsedEmail_returnsConflict() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);

    // act
    users.createUserOk(createUserRequest);
    ResponseEntity<ErrorDto> response = users.createUserKo(createUserRequest);

    // assert
    assertError(response, 409, "Conflict", "Email already used");
  }
}
