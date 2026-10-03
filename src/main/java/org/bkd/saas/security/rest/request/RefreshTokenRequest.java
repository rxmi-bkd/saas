package org.bkd.saas.security.rest.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank String refresh) {}
