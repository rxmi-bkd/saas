package org.bkd.saas.oidc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnverifiedEmailException extends RuntimeException {
  public static final String ERROR_MSG = "Email unverified: ";

  public UnverifiedEmailException(String email) {
    super(ERROR_MSG + email);
  }
}
