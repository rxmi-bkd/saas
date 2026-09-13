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
    userService.createUser(USER_EMAIL, USER_PASSWORD);
    verify(userRepository).save(any(AppUser.class));
  }

  @Test
  void createUser_shouldThrowEmailAlreadyUsedException_whenEmailIsAlreadyUsed() {
    when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(new AppUser()));
    assertThatThrownBy(() -> userService.createUser(USER_EMAIL, USER_PASSWORD)).isInstanceOf(EmailAlreadyUsedException.class);
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @Test
  void updateUserPassword_shouldEncodeAndSaveNewPassword_whenOldPasswordMatches() {
    String newPassword = "new_password";
    String newEncodedPassword = "new_encoded_password";
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(APP_USER));
    when(passwordEncoder.matches(USER_PASSWORD, USER_ENCODED_PASSWORD)).thenReturn(true);
    when(passwordEncoder.encode(newPassword)).thenReturn(newEncodedPassword);
    userService.updateUserPassword(USER_ID, USER_PASSWORD, newPassword);
    verify(userRepository).save(any(AppUser.class));
  }

  @Test
  void updateUserPassword_shouldThrowPasswordMismatchException_whenOldPasswordDoesNotMatch() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(APP_USER));
    when(passwordEncoder.matches(USER_PASSWORD, USER_ENCODED_PASSWORD)).thenReturn(false);
    assertThatThrownBy(() -> userService.updateUserPassword(USER_ID, USER_PASSWORD, "_")).isInstanceOf(PasswordMismatchException.class);
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @Test
  void updateUserPassword_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> userService.updateUserPassword(USER_ID, USER_PASSWORD, "new_password")).isInstanceOf(UserNotFoundException.class);
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @Test
  void updateUserEmail_shouldUpdateAndSaveEmail_whenEmailIsNotUsed() {
    String newEmail = "new@example.com";
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(APP_USER));
    when(userRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
    userService.updateUserEmail(USER_ID, newEmail);
    verify(userRepository).save(any(AppUser.class));
  }

  @Test
  void updateUserEmail_shouldNotThrowException_whenNewEmailIsOwnCurrentEmail() {
    APP_USER.setId(USER_ID);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(APP_USER));
    when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(APP_USER));
    userService.updateUserEmail(USER_ID, USER_EMAIL);
    verify(userRepository).save(any(AppUser.class));
  }

  @Test
  void updateUserEmail_shouldThrowEmailAlreadyUsedException_whenEmailIsUsedByAnotherUser() {
    UUID otherUserId = UUID.randomUUID();
    String otherUserEmail = "other@example.com";
    AppUser otherUser = new AppUser(otherUserEmail, USER_ENCODED_PASSWORD, USER_ROLE);
    otherUser.setId(otherUserId);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(APP_USER));
    when(userRepository.findByEmail(otherUserEmail)).thenReturn(Optional.of(otherUser));
    assertThatThrownBy(() -> userService.updateUserEmail(USER_ID, otherUserEmail)).isInstanceOf(EmailAlreadyUsedException.class);
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @Test
  void updateUserEmail_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> userService.updateUserEmail(USER_ID, USER_EMAIL)).isInstanceOf(UserNotFoundException.class);
    verify(userRepository, never()).save(any(AppUser.class));
  }
}
