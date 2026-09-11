package org.bkd.saas.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredentialsServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private CredentialsService credentialsService;

  private static final UUID USER_ID = UUID.randomUUID();
  private static final String USER_EMAIL = "user@example.com";
  private static final String USER_PASSWORD = "password";
  private static final String USER_ENCODED_PASSWORD = "encoded_password";
  private static final Role USER_ROLE = Role.ROLE_USER;
  private static final Instant NOW = Instant.now();
  private static final UserResponse USER_RESPONSE = new UserResponse(USER_ID, USER_EMAIL, USER_ROLE, NOW, NOW);

  @Test
  void verifyCredentials_shouldReturnUser_whenCredentialsAreValid() {
    AppUser appUser = new AppUser(USER_EMAIL, USER_ENCODED_PASSWORD, USER_ROLE);
    when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(appUser));
    when(passwordEncoder.matches(USER_PASSWORD, USER_ENCODED_PASSWORD)).thenReturn(true);
    when(userMapper.toResponse(appUser)).thenReturn(USER_RESPONSE);
    Optional<UserResponse> response = credentialsService.verifyCredentials(USER_EMAIL, USER_PASSWORD);
    assertThat(response).contains(USER_RESPONSE);
  }

  @Test
  void verifyCredentials_shouldReturnEmpty_whenEmailIsNotFound() {
    when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());
    Optional<UserResponse> response = credentialsService.verifyCredentials(USER_EMAIL, USER_PASSWORD);
    assertThat(response).isEmpty();
  }

  @Test
  void verifyCredentials_shouldReturnEmpty_whenPasswordDoesNotMatch() {
    AppUser appUser = new AppUser(USER_EMAIL, USER_ENCODED_PASSWORD, USER_ROLE);
    when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(appUser));
    when(passwordEncoder.matches(USER_PASSWORD, USER_ENCODED_PASSWORD)).thenReturn(false);
    Optional<UserResponse> response = credentialsService.verifyCredentials(USER_EMAIL, USER_PASSWORD);
    assertThat(response).isEmpty();
  }

  @Test
  void verifyCredentials_shouldReturnEmpty_whenUserIsDisabled() {
    AppUser appUser = new AppUser(USER_EMAIL, USER_ENCODED_PASSWORD, USER_ROLE);
    appUser.setEnabled(false);
    when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(appUser));
    when(passwordEncoder.matches(USER_PASSWORD, USER_ENCODED_PASSWORD)).thenReturn(true);
    Optional<UserResponse> response = credentialsService.verifyCredentials(USER_EMAIL, USER_PASSWORD);
    assertThat(response).isEmpty();
  }
}
