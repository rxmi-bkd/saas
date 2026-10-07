package org.bkd.saas.oidc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StateExpiredException extends RuntimeException {
  public static final String ERROR_MSG = "State expired: ";

  public StateExpiredException(String state) {
    super(ERROR_MSG + state);
  }
}
