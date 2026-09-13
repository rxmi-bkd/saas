package org.bkd.saas.reset_password.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordResetJwtService {

  @Value("${app.jwt.reset-password.secret}")
  private String secret;

  @Value("${app.jwt.reset-password.expiration}")
  private long expirationInSeconds;

  private final PasswordEncoder passwordEncoder;

  private SecretKey key;

  public static final String PASSWORD_HASH_CLAIM = "pwh";

  @PostConstruct
  public void postConstruct() {
    key = hmacShaKeyFor(secret.getBytes());
  }

  public String createJwt(UUID subject, String currentPasswordHash) {
    String subjectString = subject.toString();
    Instant now = Instant.now();
    Instant now_plus_expiration = now.plusSeconds(expirationInSeconds);
    Date issuedAt = Date.from(now);
    Date expireAt = Date.from(now_plus_expiration);

    return Jwts.builder()
               .subject(subjectString)
               .issuedAt(issuedAt)
               .expiration(expireAt)
               .claim(PASSWORD_HASH_CLAIM, passwordEncoder.encode(currentPasswordHash))
               .signWith(key)
               .compact();
  }

  public Claims readJwt(String jwt) {
    try {
      return Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
    } catch (JwtException e) {
      throw new org.bkd.saas.shared.exception.JwtException(e.getMessage());
    }
  }

  public boolean isValidJwt(String jwt, String currentPasswordHash) {
    try {
      Claims claims = readJwt(jwt);
      String pwh = claims.get(PASSWORD_HASH_CLAIM, String.class);
      return pwh != null && passwordEncoder.matches(pwh, currentPasswordHash);
    } catch (org.bkd.saas.shared.exception.JwtException e) {
      return false;
    }
  }
}
