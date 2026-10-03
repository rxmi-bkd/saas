package org.bkd.saas.oidc.service;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.security.dto.TokenPairDto;
import org.bkd.saas.security.service.AccessTokenService;
import org.bkd.saas.security.service.RefreshTokenService;
import org.bkd.saas.oidc.dto.AccessToken;
import org.bkd.saas.oidc.dto.PlatformEnum;
import org.bkd.saas.oidc.dto.ProfileDto;
import org.bkd.saas.oidc.dto.StateDto;
import org.bkd.saas.oidc.exception.StateExpiredException;
import org.bkd.saas.oidc.exception.UnsupportedPlatformException;
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
  private final List<UrlBuilder> urlBuilders;
  private final List<TokenExchanger> tokenExchangers;
  private final List<ProfileFetcher> profileFetchers;
  private final AccessTokenService accessTokenService;
  private final RefreshTokenService refreshTokenService;

  public String authorize(PlatformEnum platform) {
    UrlBuilder urlBuilder = resolve(urlBuilders, platform, "url builder");
    return urlBuilder.buildUrl();
  }

  public TokenPairDto handleCallback(String code, String state, PlatformEnum platform) {
    StateDto state_ = validateState(state);
    TokenExchanger tokenExchanger = resolve(tokenExchangers, platform, "token exchanger");
    ProfileFetcher profileFetcher = resolve(profileFetchers, platform, "profile fetcher");
    AccessToken tokens = tokenExchanger.exchangeCodeForTokens(code);
    ProfileDto profile = profileFetcher.fetchProfile(tokens);
    stateService.deleteState(state_.id());
    UserDto user = userService.readOrCreateUser(profile.email());
    String access = accessTokenService.createJwt(user.id(), user.role());
    String refresh = refreshTokenService.createToken(user.id());
    return new TokenPairDto(access, refresh);
  }

  private StateDto validateState(String state) {
    StateDto stateDto = stateService.readState(state);
    boolean isExpired = stateDto.expiresAt().isBefore(Instant.now());

    if (isExpired) {
      throw new StateExpiredException();
    }

    return stateDto;
  }

  private <T extends PlatformScoped> T resolve(
      List<T> candidates, PlatformEnum platform, String label) {
    List<T> supported = candidates.stream().filter(s -> s.supports(platform)).toList();

    if (supported.isEmpty()) {
      throw new UnsupportedPlatformException(platform);
    }

    if (supported.size() != 1) {
      throw new IllegalStateException(
          "Multiple " + label + " services found for platform: " + platform);
    }

    return supported.getFirst();
  }
}
