package org.bkd.saas.security;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bkd.saas.security.dto.TokenPairDto;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityAssertions {

  public static void assertTokenPair(ResponseEntity<TokenPairDto> response) {
    assertThat(response.getStatusCode().value()).isEqualTo(200);
    TokenPairDto tokenPair = response.getBody();
    assertThat(tokenPair).isNotNull();
    assertThat(tokenPair.access()).isNotBlank();
    assertThat(tokenPair.refresh()).isNotBlank();
  }
}
