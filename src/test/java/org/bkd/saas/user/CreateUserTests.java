package org.bkd.saas.user;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.AbstractIntegrationTests;
import org.bkd.saas.ErrorDto;
import org.bkd.saas.UserTestUtils;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.dto.RoleEnum;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
public class CreateUserTests extends AbstractIntegrationTests {
  private static final String EMAIL = "test@test.com";
  private static final String PASSWORD = "test";

  private final UserRepository userRepository;

  @BeforeEach
  void beforeEach() {
    userRepository.deleteAll();
  }

  @Test
  void createUser_returnsCreatedUser() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);

    // act
    ResponseEntity<UserDto> response = UserTestUtils.createUser(createUserRequest, UserDto.class);

    // assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
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
    UserTestUtils.createUser(createUserRequest, UserDto.class);
    ResponseEntity<ErrorDto> response =
      UserTestUtils.createUser(createUserRequest, ErrorDto.class);

    // assert
    assertThat(response.getBody().status()).isEqualTo(HttpStatus.CONFLICT.value());
  }
}
