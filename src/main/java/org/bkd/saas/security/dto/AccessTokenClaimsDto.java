package org.bkd.saas.security.dto;

import java.util.UUID;
import org.bkd.saas.user.dto.RoleEnum;

public record AccessTokenClaimsDto(UUID userId, RoleEnum role, boolean enabled) {}
