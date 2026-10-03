package org.bkd.saas.authentication.dto;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenDto(
    UUID id, UUID userId, UUID familyId, Instant expiresAt, Instant revokedAt, Instant createdAt) {}
