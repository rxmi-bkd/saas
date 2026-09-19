package org.bkd.saas.user.responses;

import java.time.Instant;
import java.util.UUID;
import org.bkd.saas.user.Role;

public record UserWithPasswordResponse(
        UUID id, String email, String password, Role role, Instant createdAt, Instant updatedAt, boolean enabled) {}
