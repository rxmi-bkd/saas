package org.bkd.saas.user;

import static org.bkd.saas.user.rest.Routes.CREATE_USER;
import static org.bkd.saas.user.rest.Routes.UPDATE_USER_EMAIL;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.shared.dto.ErrorDto;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.bkd.saas.user.rest.request.UpdateUserEmailRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class UserTestClient {
  private final RestClient restClient;

  public ResponseEntity<UserDto> createUserOk(CreateUserRequest body) {
    return createUser(body, UserDto.class);
  }

  public ResponseEntity<ErrorDto> createUserKo(CreateUserRequest body) {
    return createUser(body, ErrorDto.class);
  }

  private <T> ResponseEntity<T> createUser(CreateUserRequest body, Class<T> responseType) {
    return restClient
        .post()
        .uri(CREATE_USER)
        .contentType(APPLICATION_JSON)
        .body(body)
        .retrieve()
        .onStatus(HttpStatusCode::isError, (request, response) -> {})
        .toEntity(responseType);
  }

  public ResponseEntity<Void> updateUserEmailOk(UpdateUserEmailRequest body, String accessToken) {
    return updateUserEmail(body, accessToken, Void.class);
  }

  public ResponseEntity<ErrorDto> updateUserEmailKo(
      UpdateUserEmailRequest body, String accessToken) {
    return updateUserEmail(body, accessToken, ErrorDto.class);
  }

  private <T> ResponseEntity<T> updateUserEmail(
      UpdateUserEmailRequest body, String accessToken, Class<T> responseType) {
    return restClient
        .put()
        .uri(UPDATE_USER_EMAIL)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .contentType(APPLICATION_JSON)
        .body(body)
        .retrieve()
        .onStatus(HttpStatusCode::isError, (request, response) -> {})
        .toEntity(responseType);
  }
}
