package org.bkd.saas.user.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DisabledUserException extends RuntimeException {
  public DisabledUserException(UUID userId) {
    super("User is disabled: " + userId);
  }
}
