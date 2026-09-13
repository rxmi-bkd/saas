package org.bkd.saas.user.services;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.user.AppUser;
import org.bkd.saas.user.UserRepository;
import org.bkd.saas.user.exceptions.PasswordMismatchException;
import org.bkd.saas.user.exceptions.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserPasswordService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public void updateUserPassword(UUID userId, String newPassword) {
    AppUser user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  public void updateUserPassword(UUID userId, String oldPassword, String newPassword) {
    AppUser user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) throw new PasswordMismatchException("Passwords don't match");
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }
}
