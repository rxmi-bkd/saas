package org.bkd.saas.user;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequest(@NotBlank String oldPassword, @NotBlank String newPassword) {
}
