package org.bkd.saas.authentication;

import org.bkd.saas.jwt.JwtService;
import org.bkd.saas.user.CredentialsService;
import org.bkd.saas.user.Role;
import org.bkd.saas.user.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock
  private JwtService jwtService;

  @Mock
  private CredentialsService credentialsService;

  @InjectMocks
  private AuthenticationService authenticationService;

  private static final String ACCESS_TOKEN = "access_token";
  private static final UUID USER_ID = UUID.randomUUID();
  private static final String USER_EMAIL = "user@example.com";
  private static final String USER_PASSWORD = "password";
  private static final Role USER_ROLE = Role.ROLE_USER;

  private static final UserResponse USER = new UserResponse(USER_ID, USER_EMAIL, USER_ROLE, Instant.now(), Instant.now());

  @Test
  void login_shouldReturnAccessToken_whenCredentialsAreValid() {
    when(credentialsService.verifyCredentials(USER_EMAIL, USER_PASSWORD)).thenReturn(Optional.of(USER));
    when(jwtService.createJwt(USER_ID, Role.ROLE_USER)).thenReturn(ACCESS_TOKEN);
    LoginResponse response = authenticationService.login(USER_EMAIL, USER_PASSWORD);
    assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN);
    verify(credentialsService).verifyCredentials(USER_EMAIL, USER_PASSWORD);
    verify(jwtService).createJwt(USER_ID, Role.ROLE_USER);
  }

  @Test
  void login_shouldThrowInvalidCredentialsException_whenCredentialsAreInvalid() {
    when(credentialsService.verifyCredentials(USER_EMAIL, USER_PASSWORD)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> authenticationService.login(USER_EMAIL, USER_PASSWORD)).isInstanceOf(InvalidCredentialsException.class);
    verify(credentialsService).verifyCredentials(USER_EMAIL, USER_PASSWORD);
    verify(jwtService, never()).createJwt(any(), any());
  }
}
