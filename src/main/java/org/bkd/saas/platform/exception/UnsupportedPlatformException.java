package org.bkd.saas.platform.exception;


import org.bkd.saas.platform.Platform;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedPlatformException extends RuntimeException {

  public UnsupportedPlatformException(Platform platform) {
    super("Unsupported platform: " + platform);
  }
}
