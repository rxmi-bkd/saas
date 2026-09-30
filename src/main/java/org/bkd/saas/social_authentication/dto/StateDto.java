package org.bkd.saas.social_authentication.dto;

import java.time.Instant;
import java.util.UUID;

public record StateDto(UUID id, String value, Instant expiresAt) {}
