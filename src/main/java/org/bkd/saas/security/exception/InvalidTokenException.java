package org.bkd.saas.security.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException {
  public InvalidTokenException(String token) {
    super("Invalid token: " + token);
  }

  public InvalidTokenException(UUID tokenId) {
    super("Invalid token ID: " + tokenId);
  }
}
