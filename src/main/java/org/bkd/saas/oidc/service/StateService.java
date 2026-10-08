package org.bkd.saas.oidc.service;

import static java.time.Instant.now;
import static org.bkd.saas.shared.SecurityUtils.SECURE_RANDOM;
import static org.bkd.saas.shared.SecurityUtils.encodeToBase64;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.oidc.db.StateEntity;
import org.bkd.saas.oidc.db.StateRepository;
import org.bkd.saas.oidc.dto.StateDto;
import org.bkd.saas.oidc.exception.StateExpiredException;
import org.bkd.saas.oidc.exception.StateNotFoundException;
import org.bkd.saas.oidc.mapper.StateMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StateService {
  private static final int STATE_EXPIRATION_SECONDS = 5 * 60;

  private final StateMapper stateMapper;
  private final StateRepository stateRepository;

  public StateDto createState() {
    Instant now = now();
    Instant expiresAt = now.plusSeconds(STATE_EXPIRATION_SECONDS);
    String value = generateRandomString();

    StateEntity newStateEntity = StateEntity.builder().value(value).expiresAt(expiresAt).build();

    newStateEntity = stateRepository.save(newStateEntity);
    return stateMapper.toStateDto(newStateEntity);
  }

  public void consume(String value) {
    StateDto state = readState(value);

    if (state.expiresAt().isBefore(now())) {
      throw new StateExpiredException(value);
    }

    stateRepository.deleteById(state.id());
  }

  @Scheduled(cron = "0 */5 * * * *")
  public void deleteExpiredStates() {
    stateRepository.deleteAllByExpiresAtBefore(now());
  }

  private StateDto readState(String value) {
    return stateRepository
        .findByValue(value)
        .map(stateMapper::toStateDto)
        .orElseThrow(() -> new StateNotFoundException(value));
  }

  private String generateRandomString() {
    byte[] randomBytes = new byte[32];
    SECURE_RANDOM.nextBytes(randomBytes);
    return encodeToBase64(randomBytes);
  }
}
