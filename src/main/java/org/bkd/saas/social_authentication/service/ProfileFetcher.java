package org.bkd.saas.social_authentication.service;

import org.bkd.saas.social_authentication.dto.AccessToken;
import org.bkd.saas.social_authentication.dto.ProfileDto;

public interface ProfileFetcher extends PlatformScoped {

  ProfileDto fetchProfile(AccessToken accessToken);
}
