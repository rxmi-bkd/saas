package org.bkd.saas.social_authentication.service;

import org.bkd.saas.social_authentication.dto.AccessToken;

public interface TokenExchanger extends PlatformScoped {

  AccessToken exchangeCodeForTokens(String code);
}
