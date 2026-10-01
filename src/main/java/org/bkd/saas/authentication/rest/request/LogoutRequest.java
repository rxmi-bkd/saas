package org.bkd.saas.authentication.rest.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(@NotBlank String refresh) {}
