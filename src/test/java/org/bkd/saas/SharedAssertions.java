package org.bkd.saas;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bkd.saas.shared.dto.ErrorDto;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SharedAssertions {

  public static void assertError(
      ResponseEntity<ErrorDto> response,
      int expectedStatus,
      String expectedError,
      String expectedMessage) {
    assertThat(response.getStatusCode().value()).isEqualTo(expectedStatus);
    ErrorDto body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.status()).isEqualTo(expectedStatus);
    assertThat(body.error()).isEqualTo(expectedError);
    assertThat(body.message()).isEqualTo(expectedMessage);
  }

  public static void assertNoContent(ResponseEntity<Void> response) {
    assertThat(response.getStatusCode().value()).isEqualTo(204);
  }

  public static void assertStatus(ResponseEntity<?> response, int expectedStatus) {
    assertThat(response.getStatusCode().value()).isEqualTo(expectedStatus);
  }
}
