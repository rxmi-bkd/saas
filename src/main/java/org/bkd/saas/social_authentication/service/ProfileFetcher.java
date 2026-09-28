package org.bkd.saas.social_authentication.service;

import org.bkd.saas.social_authentication.dto.ProfileDto;
import org.bkd.saas.social_authentication.dto.TokenDto;

public interface ProfileFetcher extends PlatformScoped {

    ProfileDto fetchProfile(TokenDto tokenDto);
}
