package org.bkd.saas.social_authentication;

import lombok.RequiredArgsConstructor;
import org.bkd.fostup.account.AccountService;
import org.bkd.fostup.platform.Platform;
import org.bkd.fostup.platform.UnsupportedPlatformException;
import org.bkd.fostup.profile.Profile;
import org.bkd.fostup.profile.ProfileService;
import org.bkd.fostup.state.State;
import org.bkd.fostup.state.StateExpiredException;
import org.bkd.fostup.state.StateService;
import org.bkd.fostup.tokens.TokenManager;
import org.bkd.fostup.tokens.Tokens;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2CallbackHandler {

    private final StateService stateService;
    private final List<TokenManager> tokenManagers;
    private final List<ProfileService> profileServices;
    private final AccountService accountService;

    public void handleCallback(String code, String state, Platform platform) {
        try {
            State state_ = readAndValidateState(state);
            tenantIdentifierResolver.setTenant(state_.getApplicationId());
            TokenManager tokenManager = resolveTokenManager(platform);
            ProfileService profileService = resolveProfileService(platform);
            Tokens tokens = tokenManager.exchangeCodeForTokens(code);
            Profile profile = profileService.fetchProfile(tokens);
            accountService.createAccount(profile, tokens);
            stateService.deleteState(state_.getId());
        } finally {
            tenantIdentifierResolver.clearTenant();
        }
    }

    private ProfileService resolveProfileService(Platform platform) {
        List<ProfileService> profileServices = this.profileServices.stream()
                .filter(s -> s.supports(platform)).toList();

        if (profileServices.isEmpty()) {
            throw new UnsupportedPlatformException(platform);
        }

        if (profileServices.size() != 1) {
            throw new IllegalStateException("Multiple profile services found for platform: " + platform);
        }

        return profileServices.getFirst();
    }

    private TokenManager resolveTokenManager(Platform platform) {
        List<TokenManager> tokenManagers = this.tokenManagers.stream().filter(s -> s.supports(platform))
                .toList();

        if (tokenManagers.isEmpty()) {
            throw new UnsupportedPlatformException(platform);
        }

        if (tokenManagers.size() != 1) {
            throw new IllegalStateException("Multiple token managers found for platform: " + platform);
        }

        return tokenManagers.getFirst();
    }

    private State readAndValidateState(String state) {
        State state_ = stateService.readState(state);

        if (state_.isExpired()) {
            throw new StateExpiredException();
        }

        return state_;
    }
}
