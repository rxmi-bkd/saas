package org.bkd.saas.social_authentication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleTokenDto(@JsonProperty("access_token") String accessToken) implements TokenDto {
}
