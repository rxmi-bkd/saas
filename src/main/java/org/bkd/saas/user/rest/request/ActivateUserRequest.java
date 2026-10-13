package org.bkd.saas.user.rest.request;

import jakarta.validation.constraints.NotBlank;

public record ActivateUserRequest(@NotBlank String jwt) {
}
