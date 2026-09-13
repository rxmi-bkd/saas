package org.bkd.saas.user;

import lombok.RequiredArgsConstructor;
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
    return userMapper.toResponse(saved);
  }

  public UserResponse readUser(UUID id) {
    return userRepository.findById(id).map(userMapper::toResponse).orElseThrow(UserNotFoundException::new);
  }

  public UserResponse readUser(String email) {
    return userRepository.findByEmail(email).map(userMapper::toResponse).orElseThrow(UserNotFoundException::new);
  }

  public void updateUserPassword(UUID userId, String oldPassword, String newPassword) {
    AppUser user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) throw new PasswordMismatchException("Passwords don't match");
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
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
