package org.bkd.saas.authentication.rest.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(@NotBlank String jwt, @NotBlank String newPassword) {}
