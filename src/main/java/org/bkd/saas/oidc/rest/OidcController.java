package org.bkd.saas.oidc.rest;

import static org.bkd.saas.oidc.rest.Routes.AUTHORIZE;
import static org.bkd.saas.oidc.rest.Routes.HANDLE_CALLBACK;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.oidc.dto.PlatformEnum;
import org.bkd.saas.oidc.service.OidcService;
import org.bkd.saas.security.dto.TokenPairDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OidcController {
  private final OidcService oidcService;

  @GetMapping(AUTHORIZE)
  public ResponseEntity<String> authorize(@PathVariable PlatformEnum platform) {
    return ResponseEntity.ok(oidcService.authorize(platform));
  }

  @GetMapping(HANDLE_CALLBACK)
  public ResponseEntity<TokenPairDto> handleCallback(
      @PathVariable PlatformEnum platform, @RequestParam String code, @RequestParam String state) {
    return ResponseEntity.ok(oidcService.handleCallback(code, state, platform));
  }
}
