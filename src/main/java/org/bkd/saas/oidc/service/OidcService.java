package org.bkd.saas.oidc.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import org.bkd.saas.oidc.dto.ProviderEnum;
import org.bkd.saas.oidc.dto.ProfileDto;
import org.bkd.saas.oidc.exception.UnsupportedProviderException;
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
  private final Map<ProviderEnum, AbstractOidcService> providers;

  public OidcService(
      UserService userService,
      StateService stateService,
      AccessTokenService accessTokenService,
      List<AbstractOidcService> providers) {
    this.userService = userService;
    this.stateService = stateService;
    this.accessTokenService = accessTokenService;
    this.providers =
        providers.stream().collect(toMap(AbstractOidcService::getProvider, identity()));
  }

  public String authorize(ProviderEnum platform) {
    AbstractOidcService provider = provider(platform);
    return provider.buildUrl(stateService.createState());
  }

  public AccessTokenDto handleCallback(String code, String state, ProviderEnum platform) {
    AbstractOidcService provider = provider(platform);
    stateService.consumeState(state);
    ProfileDto profile = provider.fetchProfile(code);

    if (!profile.emailVerified()) {
      throw new UnverifiedEmailException(profile.email());
    }

    UserDto user = userService.readOrCreateUser(profile.email());
    String access = accessTokenService.createJwt(user.id(), user.role());
    return new AccessTokenDto(access);
  }

  private AbstractOidcService provider(ProviderEnum platform) {
    AbstractOidcService provider = providers.get(platform);

    if (provider == null) {
      throw new UnsupportedProviderException(platform);
    }

    return provider;
  }
}
