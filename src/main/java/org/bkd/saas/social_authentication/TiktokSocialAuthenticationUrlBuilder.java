package org.bkd.saas.social_authentication;

import lombok.RequiredArgsConstructor;
import org.bkd.fostup.platform.Platform;
import org.bkd.fostup.platform.PlatformConfiguration;
import org.bkd.fostup.platform.PlatformConfigurations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TiktokSocialAuthenticationUrlBuilder implements SocialAuthenticationUrlBuilder {

  private final PlatformConfigurations platformConfigurations;

  private final Platform platform = Platform.tiktok;

  @Override
  public boolean supports(Platform platform) {
    return platform == this.platform;
  }

  @Override
  public String buildUrl() {
    return String.format(getPlatformConfiguration().getAuthorizationUri() + "?client_key=%s&response_type=%s&scope=%s&redirect_uri=%s",
                         getPlatformConfiguration().getClientId(),
                         getPlatformConfiguration().getResponseType(),
                         getPlatformConfiguration().getScope(),
                         getPlatformConfiguration().getRedirectUri());
  }

  private PlatformConfiguration getPlatformConfiguration() {
    return platformConfigurations.getTiktok();
  }
}
