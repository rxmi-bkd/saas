package org.bkd.saas.social_authentication.service;

import org.bkd.saas.social_authentication.dto.AccessTokenDto;

public interface TokenExchanger extends PlatformScoped {

    AccessTokenDto exchangeCodeForTokens(String code);
}
