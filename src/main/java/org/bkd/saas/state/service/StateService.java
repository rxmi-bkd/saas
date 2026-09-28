package org.bkd.saas.state.service;

import lombok.RequiredArgsConstructor;
import org.bkd.saas.state.StateMapper;
import org.bkd.saas.state.db.StateEntity;
import org.bkd.saas.state.db.StateRepository;
import org.bkd.saas.state.dto.StateDto;
import org.bkd.saas.state.exception.StateNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import static java.time.Instant.now;

@Service
@Transactional
@RequiredArgsConstructor
public class StateService {
    private static final int STATE_EXPIRATION_SECONDS = 5 * 60;
    private static final SecureRandom secureRandom = new SecureRandom();

    private final StateMapper stateMapper;
    private final StateRepository stateRepository;

    public StateDto createState() {
        Instant now = now();
        Instant expiresAt = now.plusSeconds(STATE_EXPIRATION_SECONDS);
        String value = generateRandomString();

        StateEntity newStateEntity = StateEntity
                .builder()
                .value(value)
                .expiresAt(expiresAt)
                .build();

        newStateEntity = stateRepository.save(newStateEntity);
        return stateMapper.toStateDto(newStateEntity);
    }

    public StateDto readState(String value) {
        return stateRepository
                .findByValue(value)
                .map(stateMapper::toStateDto)
                .orElseThrow(StateNotFoundException::new);
    }

    public void deleteState(UUID stateId) {
        stateRepository.deleteById(stateId);
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void deleteExpiredStates() {
        stateRepository.deleteAllByExpiresAtBefore(now());
    }

    private String generateRandomString() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
