package org.bkd.saas.user.rest.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserPasswordRequest(@NotBlank String oldPassword, @NotBlank String newPassword) {}
