import { ChangeDetectionStrategy, Component, inject, input, output, signal } from '@angular/core';

import { HttpErrorResponse } from '@angular/common/http';

import { form, maxLength, minLength, required } from '@angular/forms/signals';

import { AuthenticationService } from '../../../../core/service/authentication.service';
import { PASSWORD_MAX_LENGTH, PASSWORD_MIN_LENGTH } from '../../../../core/constants/password.constants';
import { ERROR_MESSAGES } from '../../../../core/constants/error-message.constants';
import { ResetPasswordRequest } from '../../../../core/model/reset-password.model';
import { Form } from '../../../../shared/ui/form/form';
import { Input } from '../../../../shared/ui/input/input';

@Component({
  selector: 'app-reset-password-form',
  imports: [Form, Input],
  templateUrl: './reset-password-form.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResetPasswordForm {
  readonly token = input.required<string>();
  readonly reset = output<void>();
  readonly invalidToken = output<void>();

  private readonly authenticationService = inject(AuthenticationService);

  private readonly model = signal<ResetPasswordRequest>({ newPassword: '' });

  protected readonly resetPasswordForm = form(
    this.model,
    (schema) => {
      required(schema.newPassword, {
        message: ERROR_MESSAGES.required('password'),
      });
      minLength(schema.newPassword, PASSWORD_MIN_LENGTH, {
        message: ERROR_MESSAGES.minLength('password', PASSWORD_MIN_LENGTH),
      });
      maxLength(schema.newPassword, PASSWORD_MAX_LENGTH, {
        message: ERROR_MESSAGES.maxLength('password', PASSWORD_MAX_LENGTH),
      });
    },
    {
      submission: {
        action: async () => {
          try {
            await this.authenticationService.resetPassword(this.token(), this.model().newPassword);
            this.reset.emit();
            return undefined;
          } catch (error) {
            if (error instanceof HttpErrorResponse && error.status === 401) {
              this.invalidToken.emit();
              return undefined;
            }
            return {
              kind: 'reset-password',
              message: this.mapErrorToMessage(error),
            };
          }
        },
      },
    },
  );

  private mapErrorToMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      switch (error.status) {
        case 400:
          return ERROR_MESSAGES.invalidResetPasswordRequest;
        case 429:
          return ERROR_MESSAGES.tooManyAttempts;
        default:
          return ERROR_MESSAGES.serverError;
      }
    }
    return ERROR_MESSAGES.serverError;
  }
}
