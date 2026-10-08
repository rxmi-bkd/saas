package org.bkd.saas.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bkd.saas.user.dto.UserCreatedDto;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ActivationService {
  private final ActivationTokenService activationTokenService;

  @EventListener
  public void onUserCreated(UserCreatedDto user) {
    String jwt = activationTokenService.createJwt(user.id());

    // stands in for the activation email
    log.info("activation email for {}: jwt = {}", user.email(), jwt);
  }
}
