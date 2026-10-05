package org.bkd.saas.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bkd.saas.user.rest.Routes.CREATE_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bkd.saas.shared.dto.ErrorDto;
import org.bkd.saas.user.dto.RoleEnum;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTestUtils {

  private static final RestClient restClient = RestClient.builder().build();

  public static ResponseEntity<UserDto> createUserOk(CreateUserRequest body, String host) {
    return createUser(body, UserDto.class, host);
  }

  public static ResponseEntity<ErrorDto> createUserKo(CreateUserRequest body, String host) {
    return createUser(body, ErrorDto.class, host);
  }

  public static void assertUserCreated(ResponseEntity<UserDto> response, String expectedEmail) {
    assertThat(response.getStatusCode().value()).isEqualTo(201);
    UserDto user = response.getBody();
    assertThat(user).isNotNull();
    assertThat(user.id()).isNotNull();
    assertThat(user.email()).isEqualTo(expectedEmail);
    assertThat(user.role()).isEqualTo(RoleEnum.ROLE_USER);
    assertThat(user.enabled()).isTrue();
    assertThat(user.createdAt()).isNotNull();
    assertThat(user.updatedAt()).isNotNull();
  }

  private static <T> ResponseEntity<T> createUser(
      CreateUserRequest body, Class<T> responseType, String host) {
    return restClient
        .post()
        .uri(host + CREATE_USER)
        .contentType(APPLICATION_JSON)
        .body(body)
        .retrieve()
        .onStatus(HttpStatusCode::isError, (request, response) -> {})
        .toEntity(responseType);
  }
}
