package org.bkd.saas.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.util.UUID;

public record UserDto(UUID id, String email, RoleEnum role, Instant createdAt, boolean enabled,
                      @JsonIgnore int passwordVersion, @JsonIgnore String password) {
}
