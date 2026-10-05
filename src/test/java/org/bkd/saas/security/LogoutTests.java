package org.bkd.saas.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bkd.saas.SharedAssertions.assertError;
import static org.bkd.saas.SharedAssertions.assertNoContent;

import java.util.List;
import org.bkd.saas.AbstractIntegrationTests;
import org.bkd.saas.security.db.RefreshTokenEntity;
import org.bkd.saas.security.db.RefreshTokenRepository;
import org.bkd.saas.security.dto.TokenPairDto;
import org.bkd.saas.security.rest.request.LoginRequest;
import org.bkd.saas.security.rest.request.LogoutRequest;
import org.bkd.saas.shared.dto.ErrorDto;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class LogoutTests extends AbstractIntegrationTests {
  private static final String EMAIL = "test@test.com";
  private static final String PASSWORD = "test";

  @Autowired private UserRepository userRepository;
  @Autowired private RefreshTokenRepository refreshTokenRepository;

  @BeforeEach
  void beforeEach() {
    refreshTokenRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void logout_returnsNoContentAndRevokesToken() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    users.createUserOk(createUserRequest);
    TokenPairDto tokenPair = security.loginOk(loginRequest).getBody();
    LogoutRequest logoutRequest = new LogoutRequest(tokenPair.refresh());
    ResponseEntity<Void> response = security.logoutOk(logoutRequest);

    // assert
    assertNoContent(response);
    List<RefreshTokenEntity> tokens = refreshTokenRepository.findAll();
    assertThat(tokens).hasSize(1);
    assertThat(tokens.get(0).getRevokedAt()).isNotNull();
  }

  @Test
  void logout_withUnknownToken_returnsUnauthorized() {
    // arrange
    LogoutRequest logoutRequest = new LogoutRequest("unknown-token");

    // act
    ResponseEntity<ErrorDto> response = security.logoutKo(logoutRequest);

    // assert
    assertError(response, 401, "Unauthorized", "Invalid token");
  }
}
