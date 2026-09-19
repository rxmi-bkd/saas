package org.bkd.saas.authentication.services;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.user.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationTokenService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expirationInSeconds;

    private SecretKey key;

    public static final String ROLE_CLAIM = "role";

    @PostConstruct
    public void postConstruct() {
        key = hmacShaKeyFor(secret.getBytes());
    }

    public String createJwt(UUID subject, Role role) {
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
            throw new org.bkd.saas.shared.exception.JwtException(e.getMessage());
        }
    }

    public UUID readSubject(String jwt) {
        Claims claims = readJwt(jwt);
        return UUID.fromString(claims.getSubject());
    }

    public Role readRole(String jwt) {
        Claims claims = readJwt(jwt);
        String role = claims.get(ROLE_CLAIM, String.class);
        return Role.valueOf(role);
    }

    public boolean isValidJwt(String jwt) {
        try {
            readJwt(jwt);
            return true;
        } catch (org.bkd.saas.shared.exception.JwtException e) {
            return false;
        }
    }
}
