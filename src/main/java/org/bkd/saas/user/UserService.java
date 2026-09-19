package org.bkd.saas.user;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.user.exceptions.EmailAlreadyUsedException;
import org.bkd.saas.user.exceptions.PasswordMismatchException;
import org.bkd.saas.user.exceptions.UserNotFoundException;
import org.bkd.saas.user.responses.UserResponse;
import org.bkd.saas.user.responses.UserWithPasswordResponse;
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

    public UserResponse createUser(String email, String password) {
        boolean isEmailUsed = userRepository.findByEmail(email).isPresent();
        if (isEmailUsed) throw new EmailAlreadyUsedException();
        AppUser user = new AppUser(email, null, Role.ROLE_USER);
        setPassword(user, password);
        AppUser saved = userRepository.save(user);
        return userMapper.toUserResponse(saved);
    }

    public UserResponse readUser(UUID userId) {
        return userRepository
                .findById(userId)
                .map(userMapper::toUserResponse)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserResponse readUser(String email) {
        return userRepository
                .findByEmail(email)
                .map(userMapper::toUserResponse)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserWithPasswordResponse readUserWithPassword(UUID userId) {
        return userRepository
                .findById(userId)
                .map(userMapper::toUserWithPasswordResponse)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserWithPasswordResponse readUserWithPassword(String email) {
        return userRepository
                .findByEmail(email)
                .map(userMapper::toUserWithPasswordResponse)
                .orElseThrow(UserNotFoundException::new);
    }

    public Optional<UserWithPasswordResponse> readOptionalUserWithPassword(String email) {
        return userRepository.findByEmail(email).map(userMapper::toUserWithPasswordResponse);
    }

    public void updateUserEmail(UUID userId, String email) {
        AppUser user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        boolean isEmailUsedByAnotherUser = userRepository
                .findByEmail(email)
                .filter(appUser -> !appUser.getId().equals(userId))
                .isPresent();

        if (isEmailUsedByAnotherUser) throw new EmailAlreadyUsedException();

        user.setEmail(email);
        userRepository.save(user);
    }

    public void updateUserPassword(UUID userId, String newPassword) {
        AppUser user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        setPassword(user, newPassword);
        userRepository.save(user);
    }

    public void updateUserPassword(UUID userId, String oldPassword, String newPassword) {
        AppUser user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        boolean isOldPasswordCorrect = passwordEncoder.matches(oldPassword, user.getPassword());
        if (!isOldPasswordCorrect) throw new PasswordMismatchException("Old password is incorrect");
        setPassword(user, newPassword);
        userRepository.save(user);
    }

    private void setPassword(AppUser user, String password) {
        user.setPassword(passwordEncoder.encode(password));
    }
}
