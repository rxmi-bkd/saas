import { ChangeDetectionStrategy, Component, inject, input, signal } from '@angular/core';

import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import { form, maxLength, minLength, required } from '@angular/forms/signals';

import { AuthenticationService } from '../../../../core/service/authentication.service';
import { PASSWORD_MAX_LENGTH, PASSWORD_MIN_LENGTH } from '../../../../core/constants/password.constants';
import { ROUTE_URLS } from '../../../../core/constants/route.constants';
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

  private readonly router = inject(Router);
  private readonly authenticationService = inject(AuthenticationService);

  private readonly model = signal<ResetPasswordRequest>({ newPassword: '' });

  protected readonly resetPasswordForm = form(
    this.model,
    (schema) => {
      required(schema.newPassword, {
        message: ERROR_MESSAGES.required($localize`:@@field.password:password`),
      });
      minLength(schema.newPassword, PASSWORD_MIN_LENGTH, {
        message: ERROR_MESSAGES.minLength($localize`:@@field.password:password`, PASSWORD_MIN_LENGTH),
      });
      maxLength(schema.newPassword, PASSWORD_MAX_LENGTH, {
        message: ERROR_MESSAGES.maxLength($localize`:@@field.password:password`, PASSWORD_MAX_LENGTH),
      });
    },
    {
      submission: {
        action: async () => {
          try {
            await this.authenticationService.resetPassword(this.token(), this.model().newPassword);
            await this.router.navigateByUrl(ROUTE_URLS.passwordUpdated);
            return undefined;
          } catch (error) {
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
        case 401:
          return ERROR_MESSAGES.invalidResetToken;
        case 429:
          return ERROR_MESSAGES.tooManyAttempts;
        default:
          return ERROR_MESSAGES.serverError;
      }
    }
    return ERROR_MESSAGES.serverError;
  }
}
