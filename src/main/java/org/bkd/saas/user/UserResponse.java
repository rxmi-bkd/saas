package org.bkd.saas.user;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(UUID id, String email, Role role, Instant createdAt, Instant updatedAt) {
}
