package org.bkd.saas.user;

import static org.bkd.saas.shared.Routes.User.ME;
import static org.bkd.saas.shared.Routes.User.CREATE_USER;
import static org.bkd.saas.shared.Routes.User.UPDATE_USER_EMAIL;
import static org.bkd.saas.shared.Routes.User.UPDATE_USER_PASSWORD;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.user.requests.CreateUserRequest;
import org.bkd.saas.user.requests.UpdateUserEmailRequest;
import org.bkd.saas.user.requests.UpdateUserPasswordRequest;
import org.bkd.saas.user.responses.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(ME)
    public UserResponse me(@AuthenticationPrincipal UUID userId) {
        return userService.readUser(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(CREATE_USER)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request.email(), request.password());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(UPDATE_USER_PASSWORD)
    public void updateUserPassword(@AuthenticationPrincipal UUID userId, @Valid @RequestBody UpdateUserPasswordRequest request) {
        userService.updateUserPassword(userId, request.oldPassword(), request.newPassword());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(UPDATE_USER_EMAIL)
    public void updateUserEmail(@AuthenticationPrincipal UUID userId, @Valid @RequestBody UpdateUserEmailRequest request) {
        userService.updateUserEmail(userId, request.email());
    }
}
