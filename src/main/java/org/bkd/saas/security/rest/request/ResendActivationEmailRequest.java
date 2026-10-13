package org.bkd.saas.security.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendActivationEmailRequest(@NotBlank @Email String email) {
}
