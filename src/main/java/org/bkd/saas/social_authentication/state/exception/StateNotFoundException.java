package org.bkd.saas.social_authentication.state.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StateNotFoundException extends RuntimeException {
    public StateNotFoundException() {
        super("State not found");
    }
}
