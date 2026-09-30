package org.bkd.saas.social_authentication.service;

import org.bkd.saas.social_authentication.dto.AccessTokenDto;
import org.bkd.saas.social_authentication.dto.ProfileDto;

public interface ProfileFetcher extends PlatformScoped {

    ProfileDto fetchProfile(AccessTokenDto accessTokenDto);
}
