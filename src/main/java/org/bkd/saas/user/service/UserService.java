package org.bkd.saas.user.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.notification.dto.ChannelEnum;
import org.bkd.saas.notification.dto.NotificationDto;
import org.bkd.saas.notification.service.NotificationService;
import org.bkd.saas.security.service.ActivationTokenService;
import org.bkd.saas.shared.StringUtils;
import org.bkd.saas.user.db.AppUserEntity;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.dto.UserWithPasswordDto;
import org.bkd.saas.user.exception.EmailAlreadyUsedException;
import org.bkd.saas.user.exception.PasswordMismatchException;
import org.bkd.saas.user.exception.UserNotFoundException;
import org.bkd.saas.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final ActivationTokenService activationTokenService;
  private final NotificationService notificationService;

  public UserDto createUser(String email, String password) {
    String normalized = StringUtils.normalizeEmail(email);
    boolean isEmailUsed = userRepository.findByEmail(normalized).isPresent();

    if (isEmailUsed) {
      throw new EmailAlreadyUsedException(normalized);
    }

    AppUserEntity user = new AppUserEntity(normalized);
    setPassword(user, password);
    user = userRepository.save(user);
    sendActivationEmail(user.getId(), user.getEmail());
    return userMapper.toUserDto(user);
  }

  private void sendActivationEmail(UUID userId, String userEmail) {
    String jwt = activationTokenService.createJwt(userId);
    notificationService.sendNotification(
        ChannelEnum.EMAIL,
        new NotificationDto(
            userEmail, "Activate your account", "Use this token to activate your account: " + jwt));
  }

  private void setPassword(AppUserEntity user, String password) {
    user.setPassword(passwordEncoder.encode(password));
  }

  public UserDto readUser(UUID userId) {
    return userRepository
        .findById(userId)
        .map(userMapper::toUserDto)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  public UserWithPasswordDto readUserWithPassword(UUID userId) {
    return userRepository
        .findById(userId)
        .map(userMapper::toUserWithPasswordDto)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  public Optional<UserWithPasswordDto> readOptionalUserWithPassword(String email) {
    String normalized = StringUtils.normalizeEmail(email);
    return userRepository.findByEmail(normalized).map(userMapper::toUserWithPasswordDto);
  }

  public void enableUser(UUID userId) {
    AppUserEntity user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    user.setEnabled(true);
    userRepository.save(user);
  }

  public void updateUserEmail(UUID userId, String email) {
    String normalized = StringUtils.normalizeEmail(email);
    AppUserEntity user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    boolean isEmailUsedByAnotherUser =
        userRepository
            .findByEmail(normalized)
            .filter(appUserEntity -> !appUserEntity.getId().equals(userId))
            .isPresent();

    if (isEmailUsedByAnotherUser) {
      throw new EmailAlreadyUsedException(normalized);
    }

    user.setEmail(normalized);
    userRepository.save(user);
  }

  public void updateUserPassword(UUID userId, String newPassword) {
    AppUserEntity user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    setPassword(user, newPassword);
    userRepository.save(user);
  }

  public void updateUserPassword(UUID userId, String oldPassword, String newPassword) {
    AppUserEntity user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    boolean isOldPasswordCorrect = passwordEncoder.matches(oldPassword, user.getPassword());

    if (!isOldPasswordCorrect) {
      throw new PasswordMismatchException();
    }

    setPassword(user, newPassword);
    userRepository.save(user);
  }
}
