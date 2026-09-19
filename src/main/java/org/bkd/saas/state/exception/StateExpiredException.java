package org.bkd.saas.state.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StateExpiredException extends RuntimeException {

  public StateExpiredException() {
    super("State expired");
  }
}
