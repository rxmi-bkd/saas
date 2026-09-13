package org.bkd.saas.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserService userService;

  private static final UUID USER_ID = UUID.randomUUID();
  private static final String USER_EMAIL = "user@example.com";
  private static final String USER_PASSWORD = "password";
  private static final String USER_ENCODED_PASSWORD = "encoded_password";
  private static final Role USER_ROLE = Role.ROLE_USER;
  private static final Instant NOW = Instant.now();

  private static final UserResponse USER_RESPONSE = new UserResponse(USER_ID, USER_EMAIL, USER_ROLE, NOW, NOW);
  private static final AppUser APP_USER = new AppUser(USER_EMAIL, USER_ENCODED_PASSWORD, USER_ROLE);

  @Test
  void createUser_shouldCreateAndReturnUser_whenEmailIsNotUsed() {
    when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());
    when(passwordEncoder.encode(USER_PASSWORD)).thenReturn(USER_ENCODED_PASSWORD);
    when(userRepository.save(any(AppUser.class))).thenReturn(APP_USER);
    when(userMapper.toResponse(any(AppUser.class))).thenReturn(USER_RESPONSE);
    UserResponse response = userService.createUser(USER_EMAIL, USER_PASSWORD);
    assertThat(response).isEqualTo(USER_RESPONSE);
  }

  @Test
  void createUser_shouldThrowEmailAlreadyUsedException_whenEmailIsAlreadyUsed() {
    when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(new AppUser()));
    assertThatThrownBy(() -> userService.createUser(USER_EMAIL, USER_PASSWORD)).isInstanceOf(EmailAlreadyUsedException.class);
    verify(userRepository, never()).save(any());
  }
}
