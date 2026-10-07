package org.bkd.saas.security.service;

import static org.bkd.saas.shared.SecurityUtils.hash;

import io.jsonwebtoken.Claims;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.bkd.saas.security.exception.InvalidTokenException;
import org.bkd.saas.user.dto.UserWithPasswordDto;
import org.bkd.saas.user.exception.UserNotFoundException;
import org.bkd.saas.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PasswordResetTokenService {
  private final JwtService jwtService;
  private final UserService userService;

  public static final String PASSWORD_HASH_CLAIM = "pwh";

  public PasswordResetTokenService(
      @Value("${app.jwt.reset-password-token.secret}") String secret,
      @Value("${app.jwt.reset-password-token.expiration}") long expirationInSeconds,
      UserService userService) {
    this.jwtService = new JwtService(secret, expirationInSeconds);
    this.userService = userService;
  }

  public String createJwt(UUID subject, String passwordHash) {
    return jwtService.sign(subject, Map.of(PASSWORD_HASH_CLAIM, hash(passwordHash)));
  }

  public Claims readJwt(String jwt) {
    return jwtService.parse(jwt);
  }

  public boolean isValidJwt(String jwt) {
    try {
      Claims claims = readJwt(jwt);
      UserWithPasswordDto user =
          userService.readUserWithPassword(UUID.fromString(claims.getSubject()));
      String pwh = claims.get(PASSWORD_HASH_CLAIM, String.class);
      return pwh != null && Objects.equals(hash(user.password()), pwh);
    } catch (InvalidTokenException | UserNotFoundException e) {
      return false;
    }
  }
}
