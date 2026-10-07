package org.bkd.saas.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    private final static String ERROR_MSG = "User not found";

    public UserNotFoundException(String email) {
        super(ERROR_MSG + ": " + email);
    }

    public UserNotFoundException(UUID userId) {
        super(ERROR_MSG + ": " + userId);
    }
}
