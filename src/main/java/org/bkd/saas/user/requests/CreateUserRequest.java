package org.bkd.saas.user.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(@NotBlank @Email String email, @NotBlank String password) {}
