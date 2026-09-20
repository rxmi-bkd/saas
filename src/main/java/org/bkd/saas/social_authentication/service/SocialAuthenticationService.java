package org.bkd.saas.social_authentication.service;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.social_authentication.SocialAuthenticationUrlBuilder;
import org.bkd.saas.social_authentication.platform.Platform;
import org.bkd.saas.social_authentication.platform.exception.UnsupportedPlatformException;
import org.bkd.saas.social_authentication.state.dto.StateDto;
import org.bkd.saas.social_authentication.state.service.StateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialAuthenticationService {

    private final StateService stateService;
    private final List<SocialAuthenticationUrlBuilder> socialAuthenticationUrlBuilders;

    public String authorize(Platform platform) {
        SocialAuthenticationUrlBuilder urlBuilder = resolveUrlBuilder(platform);
        String url = urlBuilder.buildUrl();
        StateDto state = stateService.createState();
        return url + "&state=" + state.value();
    }

    private SocialAuthenticationUrlBuilder resolveUrlBuilder(Platform platform) {

        List<SocialAuthenticationUrlBuilder> supportedBuilder = socialAuthenticationUrlBuilders
                .stream()
                .filter(s -> s.supports(platform))
                .toList();

        if (supportedBuilder.isEmpty()) {
            throw new UnsupportedPlatformException(platform);
        }

        if (supportedBuilder.size() != 1) {
            throw new IllegalStateException("Multiple url builder services found for platform: " + platform);
        }

        return supportedBuilder.getFirst();
    }

    public void handleCallback(String code, String state, Platform platform) {
    }
}
