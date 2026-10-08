package org.bkd.saas.security.service;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.bkd.saas.security.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActivationTokenService {
  private final JwtService jwtService;

  public ActivationTokenService(
      @Value("${app.jwt.activation-token.secret}") String secret,
      @Value("${app.jwt.activation-token.expiration}") long expirationInSeconds) {

    SecretKey key = hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.jwtService = new JwtService(key, expirationInSeconds);
  }

  public String createJwt(UUID subject) {
    return jwtService.createJwt(subject, Map.of());
  }

  public UUID readSubject(String jwt) throws InvalidTokenException {
    return UUID.fromString(jwtService.readJwt(jwt).getSubject());
  }
}
