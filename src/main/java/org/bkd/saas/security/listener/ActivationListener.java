package org.bkd.saas.security.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bkd.saas.security.service.ActivationTokenService;
import org.bkd.saas.user.event.UserCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivationListener {
  private final ActivationTokenService activationTokenService;

  @EventListener
  public void onUserCreated(UserCreatedEvent user) {
    String jwt = activationTokenService.createJwt(user.id());
    log.info("activation email for {}: jwt = {}", user.email(), jwt);
  }
}
