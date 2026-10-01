package org.bkd.saas.oidc.exception;

import org.bkd.saas.oidc.dto.PlatformEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedPlatformException extends RuntimeException {

  public UnsupportedPlatformException(PlatformEnum platform) {
    super("Unsupported platform: " + platform);
  }
}
