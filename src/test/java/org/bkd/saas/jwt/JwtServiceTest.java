package org.bkd.saas.jwt;

import io.jsonwebtoken.Claims;
import org.bkd.saas.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.bkd.saas.jwt.JwtService.ROLE_CLAIM;

class JwtServiceTest {

  private static final String SECRET = "0123456789abcdef0123456789abcdef0123456789abcdef";
  private static final long EXPIRATION_IN_SECONDS = 3600L;
  private static final UUID SUBJECT = UUID.randomUUID();
  private static final Role ROLE = Role.ROLE_USER;

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    ReflectionTestUtils.setField(jwtService, "secret", SECRET);
    ReflectionTestUtils.setField(jwtService, "expirationInSeconds", EXPIRATION_IN_SECONDS);
    jwtService.postConstruct();
  }

  @Test
  void readSubject_shouldReturnSubject_whenJwtIsValid() {
    String jwt = jwtService.createJwt(SUBJECT, ROLE);
    assertThat(jwtService.readSubject(jwt)).isEqualTo(SUBJECT);
  }

  @Test
  void readRole_shouldReturnRole_whenJwtIsValid() {
    String jwt = jwtService.createJwt(SUBJECT, ROLE);
    assertThat(jwtService.readRole(jwt)).isEqualTo(ROLE);
  }

  @Test
  void readJwt_shouldReturnClaims_whenJwtIsValid() {
    String jwt = jwtService.createJwt(SUBJECT, ROLE);
    Claims claims = jwtService.readJwt(jwt);
    assertThat(claims.getSubject()).isEqualTo(SUBJECT.toString());
    assertThat(claims.get(ROLE_CLAIM, String.class)).isEqualTo(ROLE.name());
    assertThat(claims.getIssuedAt()).isNotNull();
    assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
  }

  @Test
  void readJwt_shouldThrowJwtException_whenJwtIsMalformed() {
    assertThatThrownBy(() -> jwtService.readJwt("not-a-jwt")).isInstanceOf(JwtException.class);
  }

  @Test
  void readJwt_shouldThrowJwtException_whenSignatureIsInvalid() {
    String jwt = jwtService.createJwt(SUBJECT, ROLE);
    JwtService otherJwtService = new JwtService();
    ReflectionTestUtils.setField(otherJwtService, "secret", "fedcba9876543210fedcba9876543210fedcba9876543210");
    ReflectionTestUtils.setField(otherJwtService, "expirationInSeconds", EXPIRATION_IN_SECONDS);
    otherJwtService.postConstruct();
    assertThatThrownBy(() -> otherJwtService.readJwt(jwt)).isInstanceOf(JwtException.class);
  }

  @Test
  void readJwt_shouldThrowJwtException_whenJwtIsExpired() {
    ReflectionTestUtils.setField(jwtService, "expirationInSeconds", -1L);
    String jwt = jwtService.createJwt(SUBJECT, ROLE);
    assertThatThrownBy(() -> jwtService.readJwt(jwt)).isInstanceOf(JwtException.class);
  }

  @Test
  void isValidJwt_shouldReturnTrue_whenJwtIsValid() {
    String jwt = jwtService.createJwt(SUBJECT, ROLE);
    assertThat(jwtService.isValidJwt(jwt)).isTrue();
  }

  @Test
  void isValidJwt_shouldReturnFalse_whenJwtIsInvalid() {
    assertThat(jwtService.isValidJwt("not-a-jwt")).isFalse();
  }
}
