package org.bkd.saas.social_authentication.service;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.social_authentication.platform.Platform;
import org.bkd.saas.social_authentication.platform.configuration.PlatformConfiguration;
import org.bkd.saas.social_authentication.platform.configuration.PlatformConfigurations;
import org.bkd.saas.social_authentication.state.dto.StateDto;
import org.bkd.saas.social_authentication.state.service.StateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@Transactional
@RequiredArgsConstructor
public class GoogleService implements SocialAuthenticationUrlBuilder {
    private final StateService stateService;
    private final PlatformConfigurations configurations;

    @Override
    public boolean supports(Platform platform) {
        return platform == Platform.google;
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

    private PlatformConfiguration getConfiguration() {
        return configurations.getGoogle();
    }
}
