package org.bkd.saas.oidc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProfileDto(String email, @JsonProperty("email_verified") boolean emailVerified) {}
