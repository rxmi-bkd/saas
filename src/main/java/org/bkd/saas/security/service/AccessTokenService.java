package org.bkd.saas.security.service;

import io.jsonwebtoken.Claims;
import java.util.Map;
import java.util.UUID;
import org.bkd.saas.user.dto.RoleEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccessTokenService {
  private final JwtService jwtService;

  public static final String ROLE_CLAIM = "role";

  public AccessTokenService(
      @Value("${app.jwt.authentication-token.secret}") String secret,
      @Value("${app.jwt.authentication-token.expiration}") long expirationInSeconds) {
    this.jwtService = new JwtService(secret, expirationInSeconds);
  }

  public String createJwt(UUID subject, RoleEnum role) {
    return jwtService.sign(subject, Map.of(ROLE_CLAIM, role.name()));
  }

  public Claims readJwt(String jwt) {
    return jwtService.parse(jwt);
  }

  public boolean isValidJwt(String jwt) {
    return jwtService.isValid(jwt);
  }
}
