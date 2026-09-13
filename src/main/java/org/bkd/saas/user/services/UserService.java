package org.bkd.saas.user.services;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.user.AppUser;
import org.bkd.saas.user.Role;
import org.bkd.saas.user.UserMapper;
import org.bkd.saas.user.UserRepository;
import org.bkd.saas.user.exceptions.EmailAlreadyUsedException;
import org.bkd.saas.user.exceptions.UserNotFoundException;
import org.bkd.saas.user.responses.UserResponse;
import org.bkd.saas.user.responses.UserWithPasswordResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  
  public UserResponse createUser(String email, String password) {
    if (userRepository.findByEmail(email).isPresent()) throw new EmailAlreadyUsedException();
    AppUser user = new AppUser(email, passwordEncoder.encode(password), Role.ROLE_USER);
    AppUser saved = userRepository.save(user);
    return userMapper.toUserResponse(saved);
  }

  public UserResponse readUser(UUID userId) {
    return userRepository.findById(userId).map(userMapper::toUserResponse).orElseThrow(UserNotFoundException::new);
  }

  public UserResponse readUser(String email) {
    return userRepository.findByEmail(email).map(userMapper::toUserResponse).orElseThrow(UserNotFoundException::new);
  }

  public UserWithPasswordResponse readUserWithPassword(UUID userId) {
    return userRepository.findById(userId).map(userMapper::toUserWithPasswordResponse).orElseThrow(UserNotFoundException::new);
  }

  public UserWithPasswordResponse readUserWithPassword(String email) {
    return userRepository.findByEmail(email).map(userMapper::toUserWithPasswordResponse).orElseThrow(UserNotFoundException::new);
  }

  public void updateUserEmail(UUID userId, String email) {
    AppUser user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    userRepository
        .findByEmail(email)
        .filter(appUser -> !appUser.getId().equals(userId))
        .ifPresent((appUser) -> { throw new EmailAlreadyUsedException(); });

    user.setEmail(email);
    userRepository.save(user);
  }
}
