package org.bkd.saas.security.rest.request;

import jakarta.validation.constraints.NotBlank;

public record ActivateAccountRequest(@NotBlank String jwt) {}
