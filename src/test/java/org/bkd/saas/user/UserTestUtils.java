package org.bkd.saas.user;

import static org.bkd.saas.user.rest.Routes.CREATE_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bkd.saas.ErrorDto;
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
