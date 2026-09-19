package org.bkd.saas.user.dto;

import java.time.Instant;
import java.util.UUID;
import org.bkd.saas.user.db.Role;

public record UserWithPasswordDto(UUID id, String email, String password, Role role, Instant createdAt, Instant updatedAt, boolean enabled) {}
