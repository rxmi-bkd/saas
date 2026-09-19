package org.bkd.saas.authentication.services;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.authentication.InvalidCredentialsException;
import org.bkd.saas.authentication.LoginResponse;
import org.bkd.saas.user.UserService;
import org.bkd.saas.user.responses.UserWithPasswordResponse;
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

    public LoginResponse login(String email, String password) {
        Optional<UserWithPasswordResponse> user = userService.readOptionalUserWithPassword(email);
        if(user.isEmpty()) throw new InvalidCredentialsException();
        boolean isPasswordCorrect = passwordEncoder.matches(password, user.get().password());
        if (!isPasswordCorrect) throw new InvalidCredentialsException();
        String accessToken = authenticationTokenService.createJwt(user.get().id(), user.get().role());
        return new LoginResponse(accessToken);
    }
}
