package org.bkd.saas.user.dto;

import java.time.Instant;
import java.util.UUID;
import org.bkd.saas.user.db.Role;

public record UserDto(UUID id, String email, Role role, Instant createdAt, Instant updatedAt) {}
