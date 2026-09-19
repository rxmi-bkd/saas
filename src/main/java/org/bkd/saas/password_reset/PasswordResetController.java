package org.bkd.saas.password_reset;

import static org.bkd.saas.shared.Routes.PasswordReset.FORGOT_PASSWORD;
import static org.bkd.saas.shared.Routes.PasswordReset.RESET_PASSWORD;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.password_reset.requests.ForgotPasswordRequest;
import org.bkd.saas.password_reset.requests.PasswordResetRequest;
import org.bkd.saas.password_reset.services.PasswordResetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(FORGOT_PASSWORD)
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request.email());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(RESET_PASSWORD)
    public void resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.resetPassword(request.jwt(), request.newPassword());
    }
}
