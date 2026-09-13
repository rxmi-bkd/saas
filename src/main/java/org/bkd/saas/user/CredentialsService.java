package org.bkd.saas.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CredentialsService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  public Optional<UserResponse> verifyCredentials(String email, String password) {

    return userRepository.findByEmail(email)
                         .filter(user -> passwordEncoder.matches(password, user.getPassword()) && user.isEnabled())
                         .map(userMapper::toResponse);
  }
}
