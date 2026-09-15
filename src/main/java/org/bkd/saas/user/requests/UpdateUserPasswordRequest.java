package org.bkd.saas.user.requests;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserPasswordRequest(
    @NotBlank String oldPassword, @NotBlank String newPassword) {}
