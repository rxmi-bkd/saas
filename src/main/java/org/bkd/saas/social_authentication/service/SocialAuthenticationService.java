package org.bkd.saas.social_authentication.service;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.social_authentication.platform.Platform;
import org.bkd.saas.social_authentication.platform.exception.UnsupportedPlatformException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SocialAuthenticationService {
    private final List<SocialAuthenticationUrlBuilder> socialAuthenticationUrlBuilders;

    public String authorize(Platform platform) {
        SocialAuthenticationUrlBuilder urlBuilder = resolveUrlBuilder(platform);
        return urlBuilder.buildUrl();
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
