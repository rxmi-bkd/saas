package org.bkd.saas.user.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.user.UserMapper;
import org.bkd.saas.user.db.AppUserEntity;
import org.bkd.saas.user.db.UserRepository;
import org.bkd.saas.user.dto.UserWithPasswordDto;
import org.bkd.saas.user.exception.EmailAlreadyUsedException;
import org.bkd.saas.user.exception.PasswordMismatchException;
import org.bkd.saas.user.exception.UserNotFoundException;
import org.bkd.saas.user.dto.UserDto;
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
        boolean isEmailUsed = userRepository.findByEmail(email).isPresent();
        if (isEmailUsed) throw new EmailAlreadyUsedException();
        AppUserEntity user = new AppUserEntity(email, null);
        setPassword(user, password);
        AppUserEntity saved = userRepository.save(user);
        return userMapper.toUserDto(saved);
    }

    public UserDto readUser(UUID userId) {
        return userRepository
                .findById(userId)
                .map(userMapper::toUserDto)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserDto readUser(String email) {
        return userRepository
                .findByEmail(email)
                .map(userMapper::toUserDto)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserWithPasswordDto readUserWithPassword(UUID userId) {
        return userRepository
                .findById(userId)
                .map(userMapper::toUserWithPasswordDto)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserWithPasswordDto readUserWithPassword(String email) {
        return userRepository
                .findByEmail(email)
                .map(userMapper::toUserWithPasswordDto)
                .orElseThrow(UserNotFoundException::new);
    }

    public Optional<UserWithPasswordDto> readOptionalUserWithPassword(String email) {
        return userRepository.findByEmail(email).map(userMapper::toUserWithPasswordDto);
    }

    public void updateUserEmail(UUID userId, String email) {
        AppUserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        boolean isEmailUsedByAnotherUser = userRepository
                .findByEmail(email)
                .filter(appUserEntity -> !appUserEntity.getId().equals(userId))
                .isPresent();

        if (isEmailUsedByAnotherUser) throw new EmailAlreadyUsedException();

        user.setEmail(email);
        userRepository.save(user);
    }

    public void updateUserPassword(UUID userId, String newPassword) {
        AppUserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        setPassword(user, newPassword);
        userRepository.save(user);
    }

    public void updateUserPassword(UUID userId, String oldPassword, String newPassword) {
        AppUserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        boolean isOldPasswordCorrect = passwordEncoder.matches(oldPassword, user.getPassword());
        if (!isOldPasswordCorrect) throw new PasswordMismatchException("Old password is incorrect");
        setPassword(user, newPassword);
        userRepository.save(user);
    }

    private void setPassword(AppUserEntity user, String password) {
        user.setPassword(passwordEncoder.encode(password));
    }
}
