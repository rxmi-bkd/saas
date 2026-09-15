package org.bkd.saas.user;

import static org.bkd.saas.shared.Constants.BASE_PATH;
import static org.bkd.saas.shared.Constants.PUBLIC_BASE_PATH;

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

  public static final String PRIVATE_ENDPOINT = BASE_PATH + "/users";
  public static final String PUBLIC_ENDPOINT = PUBLIC_BASE_PATH + "/users";

  @GetMapping(PRIVATE_ENDPOINT + "/me")
  public UserResponse me(@AuthenticationPrincipal UUID userId) {
    return userService.readUser(userId);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(PUBLIC_ENDPOINT)
  public UserResponse register(@Valid @RequestBody CreateUserRequest request) {
    return userService.createUser(request.email(), request.password());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping(PRIVATE_ENDPOINT + "/password")
  public void updatePassword(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody UpdateUserPasswordRequest request) {
    userService.updateUserPassword(userId, request.oldPassword(), request.newPassword());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping(PRIVATE_ENDPOINT + "/email")
  public void updateEmail(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody UpdateUserEmailRequest request) {
    userService.updateUserEmail(userId, request.email());
  }
}
