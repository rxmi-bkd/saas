package org.bkd.saas.user.event;

import java.util.UUID;

public record UserCreatedEvent(UUID id, String email) {}
