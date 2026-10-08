package org.bkd.saas.oidc.service;

import org.bkd.saas.oidc.dto.AccessToken;
import org.bkd.saas.oidc.dto.PlatformEnum;
import org.bkd.saas.oidc.dto.ProfileDto;

public interface OidcPlatform {

  boolean supports(PlatformEnum platform);

  String buildUrl();

  AccessToken exchangeCodeForTokens(String code);

  ProfileDto fetchProfile(AccessToken accessToken);
}
