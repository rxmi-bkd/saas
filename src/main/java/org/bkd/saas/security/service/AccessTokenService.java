package org.bkd.saas.security.service;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

import io.jsonwebtoken.Claims;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.bkd.saas.security.dto.AccessTokenClaimsDto;
import org.bkd.saas.security.exception.InvalidTokenException;
import org.bkd.saas.user.dto.RoleEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccessTokenService {
  private final JwtService jwtService;
  public static final String ROLE_CLAIM = "role";
  public static final String ENABLED_CLAIM = "enabled";

  public AccessTokenService(
      @Value("${app.jwt.authentication-token.secret}") String secret,
      @Value("${app.jwt.authentication-token.expiration}") long expirationInSeconds) {

    SecretKey key = hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.jwtService = new JwtService(key, expirationInSeconds);
  }

  public String createJwt(UUID subject, RoleEnum role, boolean enabled) {
    return jwtService.createJwt(subject, Map.of(ROLE_CLAIM, role.name(), ENABLED_CLAIM, enabled));
  }

  public AccessTokenClaimsDto readJwt(String jwt) {
    Claims claims = jwtService.readJwt(jwt);
    return new AccessTokenClaimsDto(
        UUID.fromString(claims.getSubject()),
        claims.get(ROLE_CLAIM, RoleEnum.class),
        claims.get(ENABLED_CLAIM, boolean.class));
  }

  public boolean isValidJwt(String jwt) {
    try {
      AccessTokenClaimsDto accessTokenClaimsDto = readJwt(jwt);
      return accessTokenClaimsDto.enabled();
    } catch (InvalidTokenException e) {
      return false;
    }
  }
}
