package org.bkd.saas.security;

import static org.bkd.saas.security.rest.Routes.LOGIN;
import static org.bkd.saas.security.rest.Routes.LOGOUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.security.dto.TokenPairDto;
import org.bkd.saas.security.rest.request.LoginRequest;
import org.bkd.saas.security.rest.request.LogoutRequest;
import org.bkd.saas.shared.dto.ErrorDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;


@RequiredArgsConstructor
public class SecurityTestClient {
  private final RestClient restClient;

  public ResponseEntity<TokenPairDto> loginOk(LoginRequest body) {
    return login(body, TokenPairDto.class);
  }

  public ResponseEntity<ErrorDto> loginKo(LoginRequest body) {
    return login(body, ErrorDto.class);
  }

  public ResponseEntity<Void> logoutOk(LogoutRequest body) {
    return logout(body, Void.class);
  }

  public ResponseEntity<ErrorDto> logoutKo(LogoutRequest body) {
    return logout(body, ErrorDto.class);
  }

  private <T> ResponseEntity<T> logout(LogoutRequest body, Class<T> responseType) {
    return restClient
        .post()
        .uri(LOGOUT)
        .contentType(APPLICATION_JSON)
        .body(body)
        .retrieve()
        .onStatus(HttpStatusCode::isError, (request, response) -> {})
        .toEntity(responseType);
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
