package org.bkd.saas.authentication.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.authentication.exception.InvalidCredentialsException;
import org.bkd.saas.authentication.dto.AuthenticationTokenDto;
import org.bkd.saas.user.service.UserService;
import org.bkd.saas.user.dto.UserWithPasswordDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationTokenService authenticationTokenService;

    public AuthenticationTokenDto login(String email, String password) {
        Optional<UserWithPasswordDto> user = userService.readOptionalUserWithPassword(email);
        if(user.isEmpty()) throw new InvalidCredentialsException();
        boolean isPasswordCorrect = passwordEncoder.matches(password, user.get().password());
        if (!isPasswordCorrect) throw new InvalidCredentialsException();
        String accessToken = authenticationTokenService.createJwt(user.get().id(), user.get().role());
        return new AuthenticationTokenDto(accessToken);
    }
}
