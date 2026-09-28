package org.bkd.saas.social_authentication.service;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.social_authentication.dto.PlatformEnum;
import org.bkd.saas.social_authentication.dto.ProfileDto;
import org.bkd.saas.social_authentication.dto.StateDto;
import org.bkd.saas.social_authentication.dto.TokenDto;
import org.bkd.saas.social_authentication.exception.StateExpiredException;
import org.bkd.saas.social_authentication.exception.UnsupportedPlatformException;
import org.bkd.saas.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SocialAuthenticationService {
    private final UserService userService;
    private final StateService stateService;
    private final List<UrlBuilder> urlBuilders;
    private final List<TokenExchanger> tokenExchangers;
    private final List<ProfileFetcher> profileFetchers;

    public String authorize(PlatformEnum platform) {
        UrlBuilder urlBuilder = resolve(urlBuilders, platform, "url builder");
        return urlBuilder.buildUrl();
    }

    public void handleCallback(String code, String state, PlatformEnum platform) {
        StateDto state_ = validateState(state);
        TokenExchanger tokenExchanger = resolve(tokenExchangers, platform, "token exchanger");
        ProfileFetcher profileFetcher = resolve(profileFetchers, platform, "profile fetcher");
        TokenDto tokens = tokenExchanger.exchangeCodeForTokens(code);
        ProfileDto profile = profileFetcher.fetchProfile(tokens);
        userService.readOrCreateUser(profile.email());
        stateService.deleteState(state_.id());
    }

    private StateDto validateState(String state) {
        StateDto stateDto = stateService.readState(state);
        boolean isExpired = stateDto.expiresAt().isBefore(Instant.now());
        if (isExpired) throw new StateExpiredException();
        return stateDto;
    }

    private <T extends PlatformScoped> T resolve(List<T> candidates, PlatformEnum platform, String label) {
        List<T> supported = candidates
                .stream()
                .filter(s -> s.supports(platform))
                .toList();

        if (supported.isEmpty()) {
            throw new UnsupportedPlatformException(platform);
        }

        if (supported.size() != 1) {
            throw new IllegalStateException("Multiple " + label + " services found for platform: " + platform);
        }

        return supported.getFirst();
    }
}
