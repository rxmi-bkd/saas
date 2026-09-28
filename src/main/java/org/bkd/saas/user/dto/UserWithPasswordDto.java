package org.bkd.saas.user.dto;

import org.bkd.saas.user.db.RoleEnum;

import java.time.Instant;
import java.util.UUID;

public record UserWithPasswordDto(UUID id, String email, String password, RoleEnum role, Instant createdAt, Instant updatedAt, boolean enabled) {}
