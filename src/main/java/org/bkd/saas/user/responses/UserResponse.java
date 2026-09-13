package org.bkd.saas.user.responses;

import org.bkd.saas.user.Role;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(UUID id, String email, Role role, Instant createdAt, Instant updatedAt) {
}
