package org.bkd.saas.oidc.service;

import org.bkd.saas.oidc.dto.AccessToken;
import org.bkd.saas.oidc.dto.ProfileDto;

public interface ProfileFetcher extends PlatformScoped {

  ProfileDto fetchProfile(AccessToken accessToken);
}
