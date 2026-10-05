package org.bkd.saas.security;

import static org.bkd.saas.security.rest.Routes.LOGIN;
import static org.bkd.saas.security.rest.Routes.LOGOUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bkd.saas.security.dto.TokenPairDto;
import org.bkd.saas.security.rest.request.LoginRequest;
import org.bkd.saas.security.rest.request.LogoutRequest;
import org.bkd.saas.shared.dto.ErrorDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationTestUtils {

  private static final RestClient restClient = RestClient.builder().build();

  public static ResponseEntity<TokenPairDto> loginOk(LoginRequest body, String host) {
    return login(body, TokenPairDto.class, host);
  }

  public static ResponseEntity<ErrorDto> loginKo(LoginRequest body, String host) {
    return login(body, ErrorDto.class, host);
  }

  public static ResponseEntity<Void> logoutOk(LogoutRequest body, String host) {
    return logout(body, Void.class, host);
  }

  public static ResponseEntity<ErrorDto> logoutKo(LogoutRequest body, String host) {
    return logout(body, ErrorDto.class, host);
  }

  private static <T> ResponseEntity<T> logout(
      LogoutRequest body, Class<T> responseType, String host) {
    return restClient
        .post()
        .uri(host + LOGOUT)
        .contentType(APPLICATION_JSON)
        .body(body)
        .retrieve()
        .onStatus(HttpStatusCode::isError, (request, response) -> {})
        .toEntity(responseType);
  }

  private static <T> ResponseEntity<T> login(
      LoginRequest body, Class<T> responseType, String host) {
    return restClient
        .post()
        .uri(host + LOGIN)
        .contentType(APPLICATION_JSON)
        .body(body)
        .retrieve()
        .onStatus(HttpStatusCode::isError, (request, response) -> {})
        .toEntity(responseType);
  }
}
