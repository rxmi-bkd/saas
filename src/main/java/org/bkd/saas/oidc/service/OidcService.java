package org.bkd.saas.oidc.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
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
public class OidcService {
  private final UserService userService;
  private final StateService stateService;
  private final AccessTokenService accessTokenService;
  private final Map<PlatformEnum, OidcProviderService> providers;

  public OidcService(
      UserService userService,
      StateService stateService,
      AccessTokenService accessTokenService,
      List<OidcProviderService> providers) {
    this.userService = userService;
    this.stateService = stateService;
    this.accessTokenService = accessTokenService;
    this.providers =
        providers.stream().collect(toMap(OidcProviderService::getPlatform, identity()));
  }

  public String authorize(PlatformEnum platform) {
    OidcProviderService provider = provider(platform);
    return provider.buildUrl(stateService.createState());
  }

  public AccessTokenDto handleCallback(String code, String state, PlatformEnum platform) {
    OidcProviderService provider = provider(platform);
    stateService.consumeState(state);
    ProfileDto profile = provider.fetchProfile(code);

    if (!profile.emailVerified()) {
      throw new UnverifiedEmailException(profile.email());
    }

    UserDto user = userService.readOrCreateUser(profile.email());
    String access = accessTokenService.createJwt(user.id(), user.role());
    return new AccessTokenDto(access);
  }

  private OidcProviderService provider(PlatformEnum platform) {
    OidcProviderService provider = providers.get(platform);

    if (provider == null) {
      throw new UnsupportedPlatformException(platform);
    }

    return provider;
  }
}
