package org.bkd.saas.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyEnabledException extends RuntimeException {
  public static final String ERROR_MSG = "User is already enabled: ";

  public UserAlreadyEnabledException(String email) {
    super(ERROR_MSG + email);
  }
}
