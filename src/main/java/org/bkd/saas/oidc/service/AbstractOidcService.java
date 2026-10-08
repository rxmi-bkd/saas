package org.bkd.saas.oidc.service;

import lombok.Getter;
import org.bkd.saas.oidc.configuration.OidcProperties;
import org.bkd.saas.oidc.configuration.ProviderProperties;
import org.bkd.saas.oidc.dto.ProviderEnum;
import org.bkd.saas.oidc.dto.ProfileDto;
import org.bkd.saas.oidc.dto.TokenDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class AbstractOidcService {
  @Getter
  private final ProviderEnum provider;
  private final RestClient restClient;
  private final ProviderProperties configuration;


  protected AbstractOidcService(ProviderEnum provider, RestClient restClient, OidcProperties properties) {
    ProviderProperties configuration = properties.providers().get(provider);

    if (configuration == null) {
      throw new IllegalStateException("Missing social authentication configuration: " + provider);
    }

    this.provider = provider;
    this.restClient = restClient;
    this.configuration = configuration;
  }

  public String buildUrl(String state) {
    return UriComponentsBuilder.fromUriString(configuration.authorizationUri())
        .queryParam("client_id", configuration.clientId())
        .queryParam("redirect_uri", configuration.redirectUri())
        .queryParam("response_type", configuration.responseType())
        .queryParam("scope", configuration.scope())
        .queryParam("state", state)
        .toUriString();
  }

  public ProfileDto fetchProfile(String code) {
    TokenDto token = exchangeCodeForToken(code);

    return restClient
        .get()
        .uri(configuration.userInfoUri())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
        .retrieve()
        .body(ProfileDto.class);
  }

  public TokenDto exchangeCodeForToken(String code) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("client_id", configuration.clientId());
    form.add("client_secret", configuration.clientSecret());
    form.add("redirect_uri", configuration.redirectUri());
    form.add("grant_type", configuration.grantType());
    form.add("code", code);

    return restClient
        .post()
        .uri(configuration.tokenUri())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(form)
        .retrieve()
        .body(TokenDto.class);
  }
}
