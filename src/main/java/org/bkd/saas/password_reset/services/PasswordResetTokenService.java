package org.bkd.saas.password_reset.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.user.UserService;
import org.bkd.saas.user.exceptions.UserNotFoundException;
import org.bkd.saas.user.responses.UserWithPasswordResponse;
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
public class PasswordResetTokenService {

  @Value("${app.jwt.reset-password.secret}")
  private String secret;

  @Value("${app.jwt.reset-password.expiration}")
  private long expirationInSeconds;

  private SecretKey key;

  public static final String PASSWORD_HASH_CLAIM = "pwh";

  private final PasswordEncoder passwordEncoder;

  private final UserService userService;

  @PostConstruct
  public void postConstruct() {
    key = hmacShaKeyFor(secret.getBytes());
  }

  public String createJwt(UUID subject, String passwordHash) {
    String subjectString = subject.toString();
    Instant now = Instant.now();
    Instant nowPlusExpiration = now.plusSeconds(expirationInSeconds);
    Date issuedAt = Date.from(now);
    Date expireAt = Date.from(nowPlusExpiration);

    return Jwts.builder()
        .subject(subjectString)
        .issuedAt(issuedAt)
        .expiration(expireAt)
        .claim(PASSWORD_HASH_CLAIM, passwordEncoder.encode(passwordHash))
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

  public UUID readSubject(String jwt) {
    Claims claims = readJwt(jwt);
    return UUID.fromString(claims.getSubject());
  }

  public UUID readSubject(Claims claims) {
    return UUID.fromString(claims.getSubject());
  }

  public boolean isValidJwt(String jwt) {
    try {
      Claims claims = readJwt(jwt);
      UUID userId = readSubject(claims);
      UserWithPasswordResponse user = userService.readUserWithPassword(userId);
      String pwh = claims.get(PASSWORD_HASH_CLAIM, String.class);
      return pwh != null && passwordEncoder.matches(user.password(), pwh);
    } catch (org.bkd.saas.shared.exception.JwtException | UserNotFoundException e) {
      return false;
    }
  }
}
