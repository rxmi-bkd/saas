package org.bkd.saas.oidc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnverifiedEmailException extends RuntimeException {
    public UnverifiedEmailException(String email) {
        super("Email unverified: " + email);
    }
}
