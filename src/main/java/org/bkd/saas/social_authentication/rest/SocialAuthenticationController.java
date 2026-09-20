package org.bkd.saas.social_authentication.rest;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.social_authentication.platform.Platform;
import org.bkd.saas.social_authentication.service.SocialAuthenticationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.bkd.saas.shared.Routes.SocialAuthentication.AUTHORIZE;
import static org.bkd.saas.shared.Routes.SocialAuthentication.HANDLE_CALLBACK;

@RestController
@RequiredArgsConstructor
public class SocialAuthenticationController {
    private final SocialAuthenticationService socialAuthenticationService;

    @GetMapping(AUTHORIZE)
    public String authorize(@PathVariable Platform platform) {
        return socialAuthenticationService.authorize(platform);
    }

    @GetMapping(HANDLE_CALLBACK)
    public void handleCallback(@PathVariable Platform platform, @RequestParam String code, @RequestParam String state) {
        socialAuthenticationService.handleCallback(code, state, platform);
    }
}
