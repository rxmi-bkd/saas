package org.bkd.saas.security.service;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.security.exception.InvalidTokenException;
import org.bkd.saas.user.dto.RoleEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccessTokenService {

  @Value("${app.jwt.authentication-token.secret}")
  private String secret;

  @Value("${app.jwt.authentication-token.expiration}")
  private long expirationInSeconds;

  private SecretKey key;

  public static final String ROLE_CLAIM = "role";

  @PostConstruct
  public void postConstruct() {
    key = hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String createJwt(UUID subject, RoleEnum role) {
    String subjectString = subject.toString();
    Instant now = Instant.now();
    Instant now_plus_expiration = now.plusSeconds(expirationInSeconds);
    Date issuedAt = Date.from(now);
    Date expireAt = Date.from(now_plus_expiration);

    return Jwts.builder()
        .subject(subjectString)
        .issuedAt(issuedAt)
        .expiration(expireAt)
        .claim(ROLE_CLAIM, role)
        .signWith(key)
        .compact();
  }

  public Claims readJwt(String jwt) {
    try {
      return Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
    } catch (JwtException e) {
      throw new InvalidTokenException(jwt);
    }
  }

  public UUID readSubject(String jwt) {
    Claims claims = readJwt(jwt);
    return UUID.fromString(claims.getSubject());
  }

  public RoleEnum readRole(String jwt) {
    Claims claims = readJwt(jwt);
    String role = claims.get(ROLE_CLAIM, String.class);
    return RoleEnum.valueOf(role);
  }

  public boolean isValidJwt(String jwt) {
    try {
      readJwt(jwt);
      return true;
    } catch (InvalidTokenException e) {
      return false;
    }
  }
}
