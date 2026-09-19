package org.bkd.saas.social_authentication;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.platform.Platform;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.bkd.saas.shared.Routes.SocialAuthentication.AUTHORIZE;
import static org.bkd.saas.shared.Routes.SocialAuthentication.HANDLE_CALLBACK;

@Validated
@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

  private final OAuth2CallbackHandler oAuth2CallbackHandler;
  private final OAuth2Initializer oAuth2Initializer;

  @GetMapping(AUTHORIZE)
  public Url authorize(@PathVariable Platform platform) {
    return oAuth2Initializer.initializeOAuth2(platform);
  }

  @GetMapping(HANDLE_CALLBACK)
  public void handleCallback(@PathVariable Platform platform, @RequestParam String code, @RequestParam String state) {
    oAuth2CallbackHandler.handleCallback(code, state, platform);
  }
}
