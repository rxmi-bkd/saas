package org.bkd.saas.oidc.service;

import org.bkd.saas.oidc.dto.AccessToken;

public interface TokenExchanger extends PlatformScoped {

  AccessToken exchangeCodeForTokens(String code);
}
