package org.bkd.saas.oidc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StateNotFoundException extends RuntimeException {
  public StateNotFoundException(String state) {
    super("State not found: " + state);
  }
}
