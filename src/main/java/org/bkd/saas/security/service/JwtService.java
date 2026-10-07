package org.bkd.saas.security.service;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.bkd.saas.security.exception.InvalidTokenException;

public class JwtService {
  private final SecretKey key;
  private final long expirationInSeconds;

  public JwtService(String secret, long expirationInSeconds) {
    this.key = hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationInSeconds = expirationInSeconds;
  }

  public String sign(UUID subject, Map<String, Object> claims) {
    Instant now = Instant.now();
    Date issuedAt = Date.from(now);
    Date expiration = Date.from(now.plusSeconds(expirationInSeconds));

    JwtBuilder builder =
        Jwts.builder().subject(subject.toString()).issuedAt(issuedAt).expiration(expiration);

    claims.forEach(builder::claim);
    return builder.signWith(key).compact();
  }

  public Claims parse(String jwt) {
    try {
      return Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
    } catch (JwtException e) {
      throw new InvalidTokenException(jwt);
    }
  }

  public boolean isValid(String jwt) {
    try {
      parse(jwt);
      return true;
    } catch (InvalidTokenException e) {
      return false;
    }
  }
}
