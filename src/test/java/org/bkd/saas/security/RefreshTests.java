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
import org.bkd.saas.security.rest.request.LoginRequest;
import org.bkd.saas.security.rest.request.RefreshTokenRequest;
import org.bkd.saas.shared.dto.ErrorDto;
import org.bkd.saas.user.db.AppUserEntity;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class RefreshTests extends AbstractIntegrationTests {
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
    assertThat(response.getBody().refresh()).isNotEqualTo(tokenPair.refresh());
    List<RefreshTokenEntity> tokens = refreshTokenRepository.findAll();
    assertThat(tokens).hasSize(2);
    assertThat(tokens.stream().filter(token -> token.getRevokedAt() == null)).hasSize(1);
    assertThat(tokens.stream().map(RefreshTokenEntity::getFamilyId).distinct()).hasSize(1);
  }

  @Test
  void refresh_withNewTokenAfterRefresh_returnsTokenPair() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    userClient.createUserOk(createUserRequest);
    TokenPairDto tokenPair = securityClient.loginOk(loginRequest).getBody();
    RefreshTokenRequest firstRequest = new RefreshTokenRequest(tokenPair.refresh());
    TokenPairDto rotated = securityClient.refreshOk(firstRequest).getBody();
    RefreshTokenRequest secondRequest = new RefreshTokenRequest(rotated.refresh());
    ResponseEntity<TokenPairDto> response = securityClient.refreshOk(secondRequest);

    // assert
    assertTokenPair(response);
  }

  @Test
  void refresh_withUnknownToken_returnsUnauthorized() {
    // arrange
    RefreshTokenRequest refreshRequest = new RefreshTokenRequest("unknown-token");

    // act
    ResponseEntity<ErrorDto> response = securityClient.refreshKo(refreshRequest);

    // assert
    assertError(response, 401, "Unauthorized", "Invalid token");
  }

  @Test
  void refresh_withRevokedTokenWithinGracePeriod_returnsUnauthorizedAndKeepsFamily() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    userClient.createUserOk(createUserRequest);
    TokenPairDto tokenPair = securityClient.loginOk(loginRequest).getBody();
    RefreshTokenRequest refreshRequest = new RefreshTokenRequest(tokenPair.refresh());
    securityClient.refreshOk(refreshRequest);
    ResponseEntity<ErrorDto> response = securityClient.refreshKo(refreshRequest);

    // assert
    assertError(response, 401, "Unauthorized", "Invalid token");
    List<RefreshTokenEntity> tokens = refreshTokenRepository.findAll();
    assertThat(tokens.stream().filter(token -> token.getRevokedAt() == null)).hasSize(1);
  }

  @Test
  void refresh_withRevokedTokenAfterGracePeriod_returnsUnauthorizedAndRevokesFamily() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    userClient.createUserOk(createUserRequest);
    TokenPairDto tokenPair = securityClient.loginOk(loginRequest).getBody();
    RefreshTokenRequest refreshRequest = new RefreshTokenRequest(tokenPair.refresh());
    TokenPairDto rotated = securityClient.refreshOk(refreshRequest).getBody();
    expireGracePeriod();
    ResponseEntity<ErrorDto> response = securityClient.refreshKo(refreshRequest);

    // assert
    assertError(response, 401, "Unauthorized", "Invalid token");
    assertThat(refreshTokenRepository.findAll())
        .allSatisfy(token -> assertThat(token.getRevokedAt()).isNotNull());
    RefreshTokenRequest rotatedRequest = new RefreshTokenRequest(rotated.refresh());
    assertError(securityClient.refreshKo(rotatedRequest), 401, "Unauthorized", "Invalid token");
  }

  @Test
  void refresh_withDisabledUser_returnsUnauthorizedAndRevokesFamily() {
    // arrange
    CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
    LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    // act
    userClient.createUserOk(createUserRequest);
    TokenPairDto tokenPair = securityClient.loginOk(loginRequest).getBody();
    disableUser(EMAIL);
    RefreshTokenRequest refreshRequest = new RefreshTokenRequest(tokenPair.refresh());
    ResponseEntity<ErrorDto> response = securityClient.refreshKo(refreshRequest);

    // assert
    assertError(response, 401, "Unauthorized", "Invalid token");
    assertThat(refreshTokenRepository.findAll())
        .allSatisfy(token -> assertThat(token.getRevokedAt()).isNotNull());
  }

  private void disableUser(String email) {
    AppUserEntity user = userRepository.findByEmail(email).orElseThrow();
    user.setEnabled(false);
    userRepository.save(user);
  }

  private void expireGracePeriod() {
    List<RefreshTokenEntity> tokens = refreshTokenRepository.findAll();
    for (RefreshTokenEntity token : tokens) {
      if (token.getRevokedAt() != null) {
        token.setRevokedAt(Instant.now().minusSeconds(3600));
        refreshTokenRepository.save(token);
      }
    }
  }
}
