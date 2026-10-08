package org.bkd.saas.oidc.dto;

import java.time.Instant;
import java.util.UUID;

public record StateDto(UUID id, String value, Instant expiresAt) {}
