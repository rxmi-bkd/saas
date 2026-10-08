package org.bkd.saas.security.dto;

import java.util.UUID;
import org.bkd.saas.user.dto.RoleEnum;

public record AuthenticationDto(UUID userId, RoleEnum role) {}
