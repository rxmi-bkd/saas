package org.bkd.saas.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyUsedException extends RuntimeException {
  public EmailAlreadyUsedException() {
    super("Email already used");
  }
}
