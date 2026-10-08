package org.bkd.saas.oidc.service;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.oidc.configuration.PlatformConfiguration;
import org.bkd.saas.oidc.configuration.PlatformConfigurations;
import org.bkd.saas.oidc.dto.*;
import org.bkd.saas.oidc.dto.AccessToken;
import org.bkd.saas.oidc.dto.GoogleTokenDto;
import org.bkd.saas.oidc.dto.PlatformEnum;
import org.bkd.saas.oidc.dto.ProfileDto;
import org.bkd.saas.oidc.dto.StateDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Transactional
@RequiredArgsConstructor
public class GoogleService implements OidcPlatform {
  private final RestClient restClient;
  private final StateService stateService;
  private final PlatformConfigurations configurations;

  @Override
  public boolean supports(PlatformEnum platform) {
    return platform == PlatformEnum.google;
  }

  @Override
  public String buildUrl() {
    StateDto state = stateService.createState();

    return UriComponentsBuilder.fromUriString(getConfiguration().authorizationUri())
        .queryParam("client_id", getConfiguration().clientId())
        .queryParam("redirect_uri", getConfiguration().redirectUri())
        .queryParam("response_type", getConfiguration().responseType())
        .queryParam("scope", getConfiguration().scope())
        .queryParam("state", state.value())
        .toUriString();
  }

  @Override
  public GoogleTokenDto exchangeCodeForTokens(String code) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("client_id", getConfiguration().clientId());
    form.add("client_secret", getConfiguration().clientSecret());
    form.add("redirect_uri", getConfiguration().redirectUri());
    form.add("grant_type", getConfiguration().grantType());
    form.add("code", code);

    return restClient
        .post()
        .uri(getConfiguration().tokenUri())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(form)
        .retrieve()
        .body(GoogleTokenDto.class);
  }

  @Override
  public ProfileDto fetchProfile(AccessToken accessToken) {
    return restClient
        .get()
        .uri(getConfiguration().userInfoUri())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.accessToken())
        .retrieve()
        .body(ProfileDto.class);
  }

  private PlatformConfiguration getConfiguration() {
    return configurations.google();
  }
}
