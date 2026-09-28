package org.bkd.saas.social_authentication.service;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.social_authentication.configuration.SocialAuthenticationConfiguration;
import org.bkd.saas.social_authentication.configuration.SocialAuthenticationConfigurations;
import org.bkd.saas.social_authentication.dto.*;
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
public class GoogleService implements UrlBuilder, TokenExchanger, ProfileFetcher {
    private final RestClient restClient;
    private final StateService stateService;
    private final SocialAuthenticationConfigurations configurations;

    @Override
    public boolean supports(PlatformEnum platform) {
        return platform == PlatformEnum.google;
    }

    @Override
    public String buildUrl() {
        StateDto state = stateService.createState();

        return UriComponentsBuilder
                .fromUriString(getConfiguration().getAuthorizationUri())
                .queryParam("client_id", getConfiguration().getClientId())
                .queryParam("redirect_uri", getConfiguration().getRedirectUri())
                .queryParam("response_type", getConfiguration().getResponseType())
                .queryParam("scope", getConfiguration().getScope())
                .queryParam("state", state.value())
                .toUriString();
    }

    @Override
    public GoogleTokenDto exchangeCodeForTokens(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", getConfiguration().getClientId());
        form.add("client_secret", getConfiguration().getClientSecret());
        form.add("redirect_uri", getConfiguration().getRedirectUri());
        form.add("grant_type", getConfiguration().getGrantType());
        form.add("code", code);

        return restClient
                .post()
                .uri(getConfiguration().getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(GoogleTokenDto.class);
    }

    @Override
    public ProfileDto fetchProfile(TokenDto tokenDto) {
        return restClient
                .get()
                .uri(getConfiguration().getUserInfoUri())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.accessToken())
                .retrieve()
                .body(ProfileDto.class);
    }

    private SocialAuthenticationConfiguration getConfiguration() {
        return configurations.getGoogle();
    }
}
