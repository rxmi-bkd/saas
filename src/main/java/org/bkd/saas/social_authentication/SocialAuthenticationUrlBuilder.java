package org.bkd.saas.social_authentication;

import org.bkd.saas.social_authentication.platform.Platform;

public interface SocialAuthenticationUrlBuilder {

    boolean supports(Platform platform);

    String buildUrl();
}
