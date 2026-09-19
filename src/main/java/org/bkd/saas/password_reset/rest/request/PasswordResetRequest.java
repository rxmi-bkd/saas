package org.bkd.saas.password_reset.rest.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(@NotBlank String jwt, @NotBlank String newPassword) {}
