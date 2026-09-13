package org.bkd.saas.user;

import org.bkd.saas.user.exceptions.PasswordMismatchException;
import org.bkd.saas.user.exceptions.UserNotFoundException;
import org.bkd.saas.user.services.UserPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPasswordServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserPasswordService userPasswordService;

  private static final UUID USER_ID = UUID.randomUUID();
  private static final String USER_EMAIL = "user@example.com";
  private static final String USER_PASSWORD = "password";
  private static final String USER_ENCODED_PASSWORD = "encoded_password";
  private static final Role USER_ROLE = Role.ROLE_USER;

  private AppUser appUser;

  @BeforeEach
  void beforeEach() {
    appUser = new AppUser(USER_EMAIL, USER_ENCODED_PASSWORD, USER_ROLE);
  }

  @Test
  void updateUserPassword_shouldEncodeAndSaveNewPassword() {
    String newPassword = "new_password";
    String newEncodedPassword = "new_encoded_password";
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(appUser));
    when(passwordEncoder.encode(newPassword)).thenReturn(newEncodedPassword);
    userPasswordService.updateUserPassword(USER_ID, newPassword);
    verify(userRepository).save(any(AppUser.class));
  }

  @Test
  void updateUserPassword_shouldEncodeAndSaveNewPassword_whenOldPasswordMatches() {
    String newPassword = "new_password";
    String newEncodedPassword = "new_encoded_password";
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(appUser));
    when(passwordEncoder.matches(USER_PASSWORD, USER_ENCODED_PASSWORD)).thenReturn(true);
    when(passwordEncoder.encode(newPassword)).thenReturn(newEncodedPassword);
    userPasswordService.updateUserPassword(USER_ID, USER_PASSWORD, newPassword);
    verify(userRepository).save(any(AppUser.class));
  }

  @Test
  void updateUserPassword_shouldThrowPasswordMismatchException_whenOldPasswordDoesNotMatch() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(appUser));
    when(passwordEncoder.matches(USER_PASSWORD, USER_ENCODED_PASSWORD)).thenReturn(false);
    assertThatThrownBy(() -> userPasswordService.updateUserPassword(USER_ID, USER_PASSWORD, "_")).isInstanceOf(PasswordMismatchException.class);
    verify(userRepository, never()).save(any(AppUser.class));
  }

  @Test
  void updateUserPassword_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> userPasswordService.updateUserPassword(USER_ID, USER_PASSWORD, "new_password")).isInstanceOf(UserNotFoundException.class);
    verify(userRepository, never()).save(any(AppUser.class));
  }
}
