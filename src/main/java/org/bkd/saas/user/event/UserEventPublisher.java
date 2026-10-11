package org.bkd.saas.user.event;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {
  private final ApplicationEventPublisher eventPublisher;

  public void publishUserCreated(UUID id, String email) {
    UserCreatedEvent event = new UserCreatedEvent(id, email);
    eventPublisher.publishEvent(event);
  }
}
