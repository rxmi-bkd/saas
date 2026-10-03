package org.bkd.saas.security.rest.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(@NotBlank String jwt, @NotBlank String newPassword) {}
