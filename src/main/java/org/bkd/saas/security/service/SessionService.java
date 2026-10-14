package org.bkd.saas.security.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SessionService {
  private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

  public void invalidateAllSessions(UUID userId) {
    invalidateOtherSessions(userId, null);
  }

  public void invalidateOtherSessions(UUID userId, String currentSessionId) {
    sessionRepository.findByPrincipalName(userId.toString())
                     .keySet()
                     .stream()
                     .filter(sessionId -> !sessionId.equals(currentSessionId))
                     .forEach(sessionRepository::deleteById);
  }
}
