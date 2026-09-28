package org.bkd.saas.user.dto;

import org.bkd.saas.user.db.RoleEnum;

import java.time.Instant;
import java.util.UUID;

public record UserDto(UUID id, String email, RoleEnum role, Instant createdAt, Instant updatedAt) {}
