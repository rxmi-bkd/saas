package org.bkd.saas.oidc.service;

import static java.time.Instant.now;
import static org.bkd.saas.shared.SecurityUtils.randomToken;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.oidc.db.StateEntity;
import org.bkd.saas.oidc.db.StateRepository;
import org.bkd.saas.oidc.exception.StateExpiredException;
import org.bkd.saas.oidc.exception.StateNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StateService {
  private static final int STATE_EXPIRATION_SECONDS = 5 * 60;

  private final StateRepository stateRepository;

  public String createState() {
    String value = randomToken();
    StateEntity state =
        StateEntity.builder()
            .value(value)
            .expiresAt(now().plusSeconds(STATE_EXPIRATION_SECONDS))
            .build();

    stateRepository.save(state);
    return value;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void consumeState(String value) {
    boolean isConsumed = stateRepository.deleteUnexpiredByValue(value, now()) > 0;

    if (isConsumed) {
      return;
    }

    if (stateRepository.existsByValue(value)) {
      throw new StateExpiredException(value);
    }

    throw new StateNotFoundException(value);
  }

  @Scheduled(cron = "0 */5 * * * *")
  public void deleteExpiredStates() {
    stateRepository.deleteAllByExpiresAtBefore(now());
  }
}
