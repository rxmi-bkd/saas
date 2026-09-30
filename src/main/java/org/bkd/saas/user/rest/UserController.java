package org.bkd.saas.user.rest;

import static org.bkd.saas.user.rest.Routes.*;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.rest.request.CreateUserRequest;
import org.bkd.saas.user.rest.request.UpdateUserEmailRequest;
import org.bkd.saas.user.rest.request.UpdateUserPasswordRequest;
import org.bkd.saas.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping(ME)
  public UserDto me(@AuthenticationPrincipal UUID userId) {
    return userService.readUser(userId);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(CREATE_USER)
  public UserDto createUser(@Valid @RequestBody CreateUserRequest request) {
    return userService.createUser(request.email(), request.password());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping(UPDATE_USER_PASSWORD)
  public void updateUserPassword(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody UpdateUserPasswordRequest request) {
    userService.updateUserPassword(userId, request.oldPassword(), request.newPassword());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping(UPDATE_USER_EMAIL)
  public void updateUserEmail(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody UpdateUserEmailRequest request) {
    userService.updateUserEmail(userId, request.email());
  }
}
