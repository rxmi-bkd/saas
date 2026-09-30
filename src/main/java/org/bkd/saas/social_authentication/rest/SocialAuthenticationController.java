package org.bkd.saas.social_authentication.rest;

import static org.bkd.saas.social_authentication.rest.Routes.AUTHORIZE;
import static org.bkd.saas.social_authentication.rest.Routes.HANDLE_CALLBACK;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.social_authentication.dto.PlatformEnum;
import org.bkd.saas.social_authentication.service.SocialAuthenticationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SocialAuthenticationController {
  private final SocialAuthenticationService socialAuthenticationService;

  @GetMapping(AUTHORIZE)
  public String authorize(@PathVariable PlatformEnum platform) {
    return socialAuthenticationService.authorize(platform);
  }

  @GetMapping(HANDLE_CALLBACK)
  public void handleCallback(
      @PathVariable PlatformEnum platform, @RequestParam String code, @RequestParam String state) {
    socialAuthenticationService.handleCallback(code, state, platform);
  }
}
