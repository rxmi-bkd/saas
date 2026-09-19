package org.bkd.saas.password_reset.services;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bkd.saas.user.UserService;
import org.bkd.saas.user.responses.UserWithPasswordResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserService userService;
    private final PasswordResetTokenService passwordResetTokenService;

    public void forgotPassword(String email) {
        Optional<UserWithPasswordResponse> user = userService.readOptionalUserWithPassword(email);
        if (user.isEmpty()) return;
        String jwt = passwordResetTokenService.createJwt(user.get().id(), user.get().password());
        log.info("jwt = {}", jwt);
    }

    public void resetPassword(String jwt, String newPassword) {
        UUID userId = passwordResetTokenService.readSubject(jwt);
        boolean isValidJwt = passwordResetTokenService.isValidJwt(jwt);
        if (isValidJwt) userService.updateUserPassword(userId, newPassword);
    }
}
