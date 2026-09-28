package org.bkd.saas.social_authentication.service;

import org.bkd.saas.social_authentication.dto.TokenDto;

public interface SocialAuthenticationTokenExchanger extends PlatformScoped {

    TokenDto exchangeCodeForTokens(String code);
}
