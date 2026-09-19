package org.bkd.saas.social_authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2Initializer {

  private final StateService stateService;
  private final List<SocialAuthenticationUrlBuilder> socialAuthenticationUrlBuilders;

  public Url initializeOAuth2(Platform platform) {
    SocialAuthenticationUrlBuilder urlBuilder = resolveUrlBuilder(platform);
    String url = urlBuilder.buildUrl();
    State state = stateService.createState();
    return new Url(url + "&state=" + state.getValue());
  }

  private SocialAuthenticationUrlBuilder resolveUrlBuilder(Platform platform) {
    List<SocialAuthenticationUrlBuilder> supportedBuilder = this.socialAuthenticationUrlBuilders.stream()
                                                                                  .filter(s -> s.supports(platform))
                                                                                  .toList();

    if (supportedBuilder.isEmpty()) {
      throw new UnsupportedPlatformException(platform);
    }

    if (supportedBuilder.size() != 1) {
      throw new IllegalStateException("Multiple url builder services found for platform: " + platform);
    }

    return supportedBuilder.getFirst();
  }
}
