package org.bkd.saas.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bkd.saas.user.rest.Routes.CREATE_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.bkd.saas.AbstractIntegrationTests;
import org.bkd.saas.DefaultErrorResponse;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.dto.RoleEnum;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

public class CreateUserTests extends AbstractIntegrationTests {
  private static final String EMAIL = "test@test.com";
  private static final String PASSWORD = "test";

  @Autowired private RestClient restClient;
  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void createUser_returnsCreatedUser() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);

    // act
    ResponseEntity<UserDto> response = createUser(createUserRequest, UserDto.class);

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
    createUser(createUserRequest, UserDto.class);
    ResponseEntity<DefaultErrorResponse> response =
        createUser(createUserRequest, DefaultErrorResponse.class);

    // assert
    assertThat(response.getBody().status()).isEqualTo(HttpStatus.CONFLICT.value());
  }

  private <T> ResponseEntity<T> createUser(CreateUserRequest body, Class<T> responseType) {
    return restClient
        .post()
        .uri(localServerUrl() + CREATE_USER)
        .contentType(APPLICATION_JSON)
        .body(body)
        .retrieve()
        .onStatus(HttpStatusCode::isError, (request, response) -> {})
        .toEntity(responseType);
  }
}
