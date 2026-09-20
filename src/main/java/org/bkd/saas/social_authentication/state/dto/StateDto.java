package org.bkd.saas.social_authentication.state.dto;

import java.time.Instant;
import java.util.UUID;

public record StateDto(UUID id, String value, Instant expiresAt) {
}
