package org.bkd.saas.jwt;

import io.jsonwebtoken.Claims;
import org.bkd.saas.authentication.services.AccessJwtService;
import org.bkd.saas.shared.exception.JwtException;
import org.bkd.saas.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.bkd.saas.authentication.services.AccessJwtService.ROLE_CLAIM;

class AccessJwtServiceTest {

  private static final String SECRET = "0123456789abcdef0123456789abcdef0123456789abcdef";
  private static final long EXPIRATION_IN_SECONDS = 3600L;
  private static final UUID SUBJECT = UUID.randomUUID();
  private static final Role ROLE = Role.ROLE_USER;

  private AccessJwtService accessJwtService;

  @BeforeEach
  void setUp() {
    accessJwtService = new AccessJwtService();
    ReflectionTestUtils.setField(accessJwtService, "secret", SECRET);
    ReflectionTestUtils.setField(accessJwtService, "expirationInSeconds", EXPIRATION_IN_SECONDS);
    accessJwtService.postConstruct();
  }

  @Test
  void readSubject_shouldReturnSubject_whenJwtIsValid() {
    String jwt = accessJwtService.createJwt(SUBJECT, ROLE);
    assertThat(accessJwtService.readSubject(jwt)).isEqualTo(SUBJECT);
  }

  @Test
  void readRole_shouldReturnRole_whenJwtIsValid() {
    String jwt = accessJwtService.createJwt(SUBJECT, ROLE);
    assertThat(accessJwtService.readRole(jwt)).isEqualTo(ROLE);
  }

  @Test
  void readJwt_shouldReturnClaims_whenJwtIsValid() {
    String jwt = accessJwtService.createJwt(SUBJECT, ROLE);
    Claims claims = accessJwtService.readJwt(jwt);
    assertThat(claims.getSubject()).isEqualTo(SUBJECT.toString());
    assertThat(claims.get(ROLE_CLAIM, String.class)).isEqualTo(ROLE.name());
    assertThat(claims.getIssuedAt()).isNotNull();
    assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
  }

  @Test
  void readJwt_shouldThrowJwtException_whenJwtIsMalformed() {
    assertThatThrownBy(() -> accessJwtService.readJwt("not-a-jwt")).isInstanceOf(JwtException.class);
  }

  @Test
  void readJwt_shouldThrowJwtException_whenSignatureIsInvalid() {
    String jwt = accessJwtService.createJwt(SUBJECT, ROLE);
    AccessJwtService otherAccessJwtService = new AccessJwtService();
    ReflectionTestUtils.setField(otherAccessJwtService, "secret", "fedcba9876543210fedcba9876543210fedcba9876543210");
    ReflectionTestUtils.setField(otherAccessJwtService, "expirationInSeconds", EXPIRATION_IN_SECONDS);
    otherAccessJwtService.postConstruct();
    assertThatThrownBy(() -> otherAccessJwtService.readJwt(jwt)).isInstanceOf(JwtException.class);
  }

  @Test
  void readJwt_shouldThrowJwtException_whenJwtIsExpired() {
    ReflectionTestUtils.setField(accessJwtService, "expirationInSeconds", -1L);
    String jwt = accessJwtService.createJwt(SUBJECT, ROLE);
    assertThatThrownBy(() -> accessJwtService.readJwt(jwt)).isInstanceOf(JwtException.class);
  }

  @Test
  void isValidJwt_shouldReturnTrue_whenJwtIsValid() {
    String jwt = accessJwtService.createJwt(SUBJECT, ROLE);
    assertThat(accessJwtService.isValidJwt(jwt)).isTrue();
  }

  @Test
  void isValidJwt_shouldReturnFalse_whenJwtIsInvalid() {
    assertThat(accessJwtService.isValidJwt("not-a-jwt")).isFalse();
  }
}
