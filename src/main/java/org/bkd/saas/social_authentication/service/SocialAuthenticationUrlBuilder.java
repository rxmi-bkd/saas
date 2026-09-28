package org.bkd.saas.social_authentication.service;

import org.bkd.saas.platform.Platform;

public interface SocialAuthenticationUrlBuilder {

    boolean supports(Platform platform);

    String buildUrl();
}
