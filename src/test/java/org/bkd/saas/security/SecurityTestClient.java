package org.bkd.saas.security;

import static org.bkd.saas.security.rest.Routes.LOGIN;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.security.dto.AccessTokenDto;
import org.bkd.saas.security.rest.request.LoginRequest;
import org.bkd.saas.shared.dto.ErrorDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class SecurityTestClient {
  private final RestClient restClient;

  public ResponseEntity<AccessTokenDto> loginOk(LoginRequest body) {
    return login(body, AccessTokenDto.class);
  }

  public ResponseEntity<ErrorDto> loginKo(LoginRequest body) {
    return login(body, ErrorDto.class);
  }

  private <T> ResponseEntity<T> login(LoginRequest body, Class<T> responseType) {
    return restClient
        .post()
        .uri(LOGIN)
        .contentType(APPLICATION_JSON)
        .body(body)
        .retrieve()
        .onStatus(HttpStatusCode::isError, (request, response) -> {})
        .toEntity(responseType);
  }
}
