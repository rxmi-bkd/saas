package org.bkd.saas.security;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bkd.saas.security.dto.AccessTokenDto;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityAssertions {

  public static void assertAccessToken(ResponseEntity<AccessTokenDto> response) {
    assertThat(response.getStatusCode().value()).isEqualTo(200);
    AccessTokenDto accessToken = response.getBody();
    assertThat(accessToken).isNotNull();
    assertThat(accessToken.access()).isNotBlank();
  }
}
