package org.bkd.saas.security.service;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static org.bkd.saas.shared.SecurityUtils.hash;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.security.exception.InvalidTokenException;
import org.bkd.saas.user.dto.UserWithPasswordDto;
import org.bkd.saas.user.exception.UserNotFoundException;
import org.bkd.saas.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordResetTokenService {

  @Value("${app.jwt.reset-password-token.secret}")
  private String secret;

  @Value("${app.jwt.reset-password-token.expiration}")
  private long expirationInSeconds;

  private SecretKey key;

  public static final String PASSWORD_HASH_CLAIM = "pwh";

  private final UserService userService;

  @PostConstruct
  public void postConstruct() {
    key = hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
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
        .claim(PASSWORD_HASH_CLAIM, hash(passwordHash))
        .signWith(key)
        .compact();
  }

  public Claims readJwt(String jwt) {
    try {
      return Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
    } catch (JwtException e) {
      throw new InvalidTokenException();
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
      UserWithPasswordDto user = userService.readUserWithPassword(userId);
      String pwh = claims.get(PASSWORD_HASH_CLAIM, String.class);
      return pwh != null && Objects.equals(hash(user.password()), pwh);
    } catch (InvalidTokenException | UserNotFoundException e) {
      return false;
    }
  }
}
