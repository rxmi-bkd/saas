package org.bkd.saas.user.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

  public UserDto createUser(String email, String password) {
    String normalized = StringUtils.normalizeEmail(email);
    boolean isEmailUsed = userRepository.findByEmail(normalized).isPresent();

    if (isEmailUsed) {
      throw new EmailAlreadyUsedException(normalized);
    }

    AppUserEntity user = new AppUserEntity(normalized);
    setPassword(user, password);
    AppUserEntity saved = userRepository.save(user);
    return userMapper.toUserDto(saved);
  }

  public UserDto createUser(String email) {
    return createUser(email, null);
  }

  public UserDto readUser(UUID userId) {
    return userRepository
        .findById(userId)
        .map(userMapper::toUserDto)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  public UserDto readOrCreateUser(String email) {
    String normalized = StringUtils.normalizeEmail(email);
    return userRepository
        .findByEmail(normalized)
        .map(userMapper::toUserDto)
        .orElseGet(() -> createUser(normalized));
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

  private void setPassword(AppUserEntity user, String password) {
    user.setPassword(passwordEncoder.encode(password));
  }
}
