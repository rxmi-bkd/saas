package org.bkd.saas.user.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.notification.service.EmailService;
import org.bkd.saas.security.service.ActivationTokenService;
import org.bkd.saas.security.service.SessionService;
import org.bkd.saas.shared.StringUtils;
import org.bkd.saas.user.db.AppUserEntity;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.exception.EmailAlreadyUsedException;
import org.bkd.saas.user.exception.PasswordMismatchException;
import org.bkd.saas.user.exception.UserNotFoundException;
import org.bkd.saas.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final EmailService emailService;
  private final ActivationTokenService activationTokenService;
  private final SessionService sessionService;

  @Transactional
  public UserDto createUser(String email, String password) {
    String normalized = StringUtils.normalizeEmail(email);
    boolean isEmailUsed = userRepository.findByEmail(normalized).isPresent();

    if (isEmailUsed) {
      throw new EmailAlreadyUsedException(normalized);
    }

    AppUserEntity user = new AppUserEntity(normalized);
    setPassword(user, password);
    user = userRepository.save(user);
    emailService.sendActivationEmail(user.getId(), user.getEmail());
    return userMapper.toUserDto(user);
  }

  private void setPassword(AppUserEntity user, String password) {
    user.setPassword(passwordEncoder.encode(password));
    user.setPasswordVersion(user.getPasswordVersion() + 1);
  }

  public UserDto readUser(UUID userId) {
    return userRepository.findById(userId)
                         .map(userMapper::toUserDto)
                         .orElseThrow(() -> new UserNotFoundException(userId));
  }

  public Optional<UserDto> readOptionalUser(String email) {
    String normalized = StringUtils.normalizeEmail(email);
    return userRepository.findByEmail(normalized).map(userMapper::toUserDto);
  }

  @Transactional
  public void activateUser(String jwt) {
    UUID userId = activationTokenService.readSubject(jwt);
    enableUser(userId);
  }

  @Transactional
  public void enableUser(UUID userId) {
    AppUserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    user.setEnabled(true);
    userRepository.save(user);
  }

  /**
   * @Transactional
   *                public void updateUserEmail(UUID userId, String email) {
   *                String normalized = StringUtils.normalizeEmail(email);
   *                AppUserEntity user = userRepository.findById(userId).orElseThrow(() -> new
   *                UserNotFoundException(userId));
   * 
   *                boolean isEmailUsedByAnotherUser = userRepository.findByEmail(normalized)
   *                .filter(appUserEntity -> !appUserEntity.getId().equals(userId))
   *                .isPresent();
   * 
   *                if (isEmailUsedByAnotherUser) {
   *                throw new EmailAlreadyUsedException(normalized);
   *                }
   * 
   *                user.setEmail(normalized);
   *                userRepository.save(user);
   *                }
   **/

  @Transactional
  public void updateUserPassword(UUID userId, String newPassword) {
    AppUserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    setPassword(user, newPassword);
    userRepository.save(user);
    sessionService.invalidateAllSessions(userId);
  }

  @Transactional
  public void updateUserPassword(UUID userId, String oldPassword, String newPassword, String currentSessionId) {
    AppUserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    boolean isOldPasswordCorrect = passwordEncoder.matches(oldPassword, user.getPassword());

    if (!isOldPasswordCorrect) {
      throw new PasswordMismatchException();
    }

    setPassword(user, newPassword);
    userRepository.save(user);
    sessionService.invalidateOtherSessions(userId, currentSessionId);
  }
}
