package org.bkd.saas.user;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.shared.exception.ResourceNotFoundException;
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

  private final ResourceNotFoundException userNotFoundException = new ResourceNotFoundException("User not found");

  public UserResponse createUser(String email, String password) {
    if (userRepository.findByEmail(email).isPresent()) throw new EmailAlreadyUsedException(email);
    AppUser user = new AppUser(email, passwordEncoder.encode(password), Role.ROLE_USER);
    AppUser saved = userRepository.save(user);
    return userMapper.toResponse(saved);
  }

  public UserResponse readUser(UUID id) {
    return userRepository.findById(id).map(userMapper::toResponse).orElseThrow(() -> userNotFoundException);
  }

  public UserResponse readUser(String email) {
    return userRepository.findByEmail(email).map(userMapper::toResponse).orElseThrow(() -> userNotFoundException);
  }
}
