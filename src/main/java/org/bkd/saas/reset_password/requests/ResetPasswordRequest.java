package org.bkd.saas.reset_password.requests;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(@NotBlank String jwt, @NotBlank String newPassword) {
}
