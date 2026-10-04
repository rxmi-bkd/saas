package org.bkd.saas.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.bkd.saas.AbstractIntegrationTests;
import org.bkd.saas.ErrorDto;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.dto.RoleEnum;
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
    ResponseEntity<UserDto> response = createUserOk(createUserRequest);

    // assert
    assertThat(response.getStatusCode().value()).isEqualTo(201);
    UserDto createdUser = response.getBody();
    assertThat(createdUser.id()).isNotNull();
    assertThat(createdUser.email()).isEqualTo(EMAIL);
    assertThat(createdUser.role()).isEqualTo(RoleEnum.ROLE_USER);
    assertThat(createdUser.enabled()).isTrue();
    assertThat(createdUser.createdAt()).isNotNull();
    assertThat(createdUser.updatedAt()).isNotNull();
  }

  @Test
  void createUser_withAlreadyUsedEmail_returnsConflict() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);

    // act
    createUserOk(createUserRequest);
    ResponseEntity<ErrorDto> response = createUserKo(createUserRequest);

    // assert
    assertThat(response.getBody().status()).isEqualTo(409);
    assertThat(response.getBody().message()).isEqualTo("Email already used");
    assertThat(response.getBody().error()).isEqualTo("Conflict");
  }

  private ResponseEntity<UserDto> createUserOk(CreateUserRequest request) {
    return UserTestUtils.createUserOk(request, server());
  }

  private ResponseEntity<ErrorDto> createUserKo(CreateUserRequest request) {
    return UserTestUtils.createUserKo(request, server());
  }
}
