package org.bkd.saas.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
}
