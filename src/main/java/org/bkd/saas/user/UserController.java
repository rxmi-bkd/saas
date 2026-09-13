package org.bkd.saas.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

  @GetMapping("/api/users/me")
  public UserResponse me(@AuthenticationPrincipal UUID userId) {
    return userService.readUser(userId);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/api/public/users")
  public UserResponse register(@Valid @RequestBody RegisterRequest request) {
    return userService.createUser(request.email(), request.password());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping("/api/users/password")
  public void updatePassword(@AuthenticationPrincipal UUID userId, @Valid @RequestBody UpdatePasswordRequest request) {
    userService.updateUserPassword(userId, request.oldPassword(), request.newPassword());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping("/api/users/email")
  public void updateEmail(@AuthenticationPrincipal UUID userId, @Valid @RequestBody UpdateEmailRequest request) {
    userService.updateUserEmail(userId, request.email());
  }
}
