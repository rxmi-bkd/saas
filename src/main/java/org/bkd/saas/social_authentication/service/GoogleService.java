package org.bkd.saas.social_authentication.service;

import org.bkd.saas.social_authentication.SocialAuthenticationUrlBuilder;
import org.bkd.saas.social_authentication.platform.Platform;
import org.springframework.stereotype.Service;

@Service
public class GoogleService implements SocialAuthenticationUrlBuilder {

    @Override
    public boolean supports(Platform platform) {
        return platform == Platform.google;
    }

    @Override
    public String buildUrl() {
        return "";
    }
}
