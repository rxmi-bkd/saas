package org.bkd.saas.oidc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.oidc.dto.AccessToken;
import org.bkd.saas.oidc.dto.PlatformEnum;
import org.bkd.saas.oidc.dto.ProfileDto;
import org.bkd.saas.oidc.exception.UnsupportedPlatformException;
import org.bkd.saas.oidc.exception.UnverifiedEmailException;
import org.bkd.saas.security.dto.AccessTokenDto;
import org.bkd.saas.security.service.AccessTokenService;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OidcService {
  private final UserService userService;
  private final StateService stateService;
  private final List<OidcPlatform> platforms;
  private final AccessTokenService accessTokenService;

  public String authorize(PlatformEnum platform) {
    return resolve(platform).buildUrl();
  }

  public AccessTokenDto handleCallback(String code, String state, PlatformEnum platform) {
    stateService.consume(state);
    OidcPlatform oidcPlatform = resolve(platform);
    AccessToken tokens = oidcPlatform.exchangeCodeForTokens(code);
    ProfileDto profile = oidcPlatform.fetchProfile(tokens);

    if (!profile.emailVerified()) {
      throw new UnverifiedEmailException(profile.email());
    }

    UserDto user = userService.readOrCreateUser(profile.email());
    String access = accessTokenService.createJwt(user.id(), user.role());
    return new AccessTokenDto(access);
  }

  private OidcPlatform resolve(PlatformEnum platform) {
    List<OidcPlatform> supported = platforms.stream().filter(p -> p.supports(platform)).toList();

    if (supported.isEmpty()) {
      throw new UnsupportedPlatformException(platform);
    }

    if (supported.size() != 1) {
      throw new IllegalStateException("Multiple services found for platform: " + platform);
    }

    return supported.getFirst();
  }
}
