package org.bkd.saas.user.responses;

import java.time.Instant;
import java.util.UUID;
import org.bkd.saas.user.Role;

public record UserResponse(
    UUID id, String email, Role role, Instant createdAt, Instant updatedAt) {}
