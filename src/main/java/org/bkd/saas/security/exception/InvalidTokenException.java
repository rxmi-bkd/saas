package org.bkd.saas.security.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException {
  public static final String ERROR_MSG = "Invalid token: ";
  public static final String ID_ERROR_MSG = "Invalid token ID: ";

  public InvalidTokenException(String token) {
    super(ERROR_MSG + token);
  }

  public InvalidTokenException(UUID tokenId) {
    super(ID_ERROR_MSG + tokenId);
  }
}
