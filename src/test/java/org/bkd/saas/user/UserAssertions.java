package org.bkd.saas.user;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bkd.saas.user.dto.RoleEnum;
import org.bkd.saas.user.dto.UserDto;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAssertions {

  public static void assertUserCreated(ResponseEntity<UserDto> response, String expectedEmail) {
    assertThat(response.getStatusCode().value()).isEqualTo(201);
    UserDto user = response.getBody();
    assertThat(user).isNotNull();
    assertThat(user.id()).isNotNull();
    assertThat(user.email()).isEqualTo(expectedEmail);
    assertThat(user.role()).isEqualTo(RoleEnum.ROLE_USER);
    assertThat(user.enabled()).isTrue();
    assertThat(user.createdAt()).isNotNull();
    assertThat(user.updatedAt()).isNotNull();
  }
}
