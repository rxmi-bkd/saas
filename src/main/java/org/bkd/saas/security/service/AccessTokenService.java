package org.bkd.saas.security.service;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

import io.jsonwebtoken.Claims;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.bkd.saas.security.dto.AuthenticationDto;
import org.bkd.saas.user.dto.RoleEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

    SecretKey key = hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.jwtService = new JwtService(key, expirationInSeconds);
  }

  public String createJwt(UUID subject, RoleEnum role) {
    return jwtService.sign(subject, Map.of(ROLE_CLAIM, role.name()));
  }

  public AuthenticationDto readJwt(String jwt) {
    Claims claims = jwtService.parse(jwt);
    return new AuthenticationDto(
        UUID.fromString(claims.getSubject()), claims.get(ROLE_CLAIM, RoleEnum.class));
  }

  public boolean isValidJwt(String jwt) {
    return jwtService.isValid(jwt);
  }
}
