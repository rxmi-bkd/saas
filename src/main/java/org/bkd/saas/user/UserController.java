package org.bkd.saas.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.user.requests.RegisterRequest;
import org.bkd.saas.user.requests.UpdateEmailRequest;
import org.bkd.saas.user.requests.UpdatePasswordRequest;
import org.bkd.saas.user.responses.UserResponse;
import org.bkd.saas.user.services.PasswordService;
import org.bkd.saas.user.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final PasswordService passwordService;

  public static final String PRIVATE_ENDPOINT = "/api/users";
  public static final String PUBLIC_ENDPOINT = "/api/public/users";

  @GetMapping(PRIVATE_ENDPOINT + "/me")
  public UserResponse me(@AuthenticationPrincipal UUID userId) {
    return userService.readUser(userId);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(PUBLIC_ENDPOINT)
  public UserResponse register(@Valid @RequestBody RegisterRequest request) {
    return userService.createUser(request.email(), request.password());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping(PRIVATE_ENDPOINT + "/password")
  public void updatePassword(@AuthenticationPrincipal UUID userId, @Valid @RequestBody UpdatePasswordRequest request) {
    passwordService.updateUserPassword(userId, request.oldPassword(), request.newPassword());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping(PRIVATE_ENDPOINT + "/email")
  public void updateEmail(@AuthenticationPrincipal UUID userId, @Valid @RequestBody UpdateEmailRequest request) {
    userService.updateUserEmail(userId, request.email());
  }
}
