package org.bkd.saas.oidc.exception;

import org.bkd.saas.oidc.dto.ProviderEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedProviderException extends RuntimeException {
  public static final String ERROR_MSG = "Unsupported provider: ";

  public UnsupportedProviderException(ProviderEnum provider) {
    super(ERROR_MSG + provider);
  }
}
