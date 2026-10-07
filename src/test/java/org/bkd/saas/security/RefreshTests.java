package org.bkd.saas.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bkd.saas.SharedAssertions.assertError;
import static org.bkd.saas.security.SecurityAssertions.assertTokenPair;

import java.time.Instant;
import java.util.List;
import org.bkd.saas.AbstractIntegrationTests;
import org.bkd.saas.security.db.RefreshTokenEntity;
import org.bkd.saas.security.db.RefreshTokenRepository;
import org.bkd.saas.security.dto.TokenPairDto;
import org.bkd.saas.security.exception.InvalidTokenException;
import org.bkd.saas.security.rest.request.LoginRequest;
import org.bkd.saas.security.rest.request.RefreshTokenRequest;
import org.bkd.saas.security.service.RefreshTokenService;
import org.bkd.saas.shared.dto.ErrorDto;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

public class RefreshTests extends AbstractIntegrationTests {
  private static final String EMAIL = "test@test.com";
  private static final String PASSWORD = "test";

  @Autowired private UserRepository userRepository;
  @Autowired private RefreshTokenRepository refreshTokenRepository;
  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private RefreshTokenService refreshTokenService;

  @BeforeEach
  void beforeEach() {
    refreshTokenRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void refresh_returnsNewTokenPairAndRevokesOldToken() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    userClient.createUserOk(createUserRequest);
    TokenPairDto tokenPair = securityClient.loginOk(loginRequest).getBody();
    RefreshTokenRequest refreshRequest = new RefreshTokenRequest(tokenPair.refresh());
    ResponseEntity<TokenPairDto> response = securityClient.refreshOk(refreshRequest);

    // assert
    assertTokenPair(response);
    List<RefreshTokenEntity> tokens = refreshTokenRepository.findAll();
    assertThat(tokens).hasSize(2);
    assertThat(tokens.stream().filter(RefreshTests::isRevoked)).hasSize(1);
  }

  @Test
  void refresh_withUnknownToken_returnsUnauthorized() {
    // arrange
    RefreshTokenRequest refreshRequest = new RefreshTokenRequest("unknown-token");

    // act
    ResponseEntity<ErrorDto> response = securityClient.refreshKo(refreshRequest);

    // assert
    assertError(
        response, 401, "Unauthorized", InvalidTokenException.ERROR_MSG + refreshRequest.refresh());
  }

  @Test
  void refresh_withExpiredToken_returnsUnauthorized() {
    // arrange
    long baseValue = (long) ReflectionTestUtils.getField(refreshTokenService, "expirationInSeconds");
    ReflectionTestUtils.setField(refreshTokenService, "expirationInSeconds", -1l);
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    userClient.createUserOk(createUserRequest);
    TokenPairDto tokenPair = securityClient.loginOk(loginRequest).getBody();
    RefreshTokenRequest refreshRequest = new RefreshTokenRequest(tokenPair.refresh());
    ResponseEntity<ErrorDto> response = securityClient.refreshKo(refreshRequest);

    // assert
    assertError(
        response, 401, "Unauthorized", InvalidTokenException.ERROR_MSG + refreshRequest.refresh());

    // clean up
    ReflectionTestUtils.setField(refreshTokenService, "expirationInSeconds", baseValue);
  }

  private static boolean isRevoked(RefreshTokenEntity refreshTokenEntity) {
    return refreshTokenEntity.getRevokedAt() != null;
  }
}
