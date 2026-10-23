import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';

import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';

import { email, form, required } from '@angular/forms/signals';

import { AuthenticationService } from '../../../../core/service/authentication.service';
import { ROUTE_URLS } from '../../../../core/constants/route.constants';
import { ErrorMessageService } from '../../../../core/service/error-message.service';
import { Form } from '../../../../shared/ui/form/form';
import { Input } from '../../../../shared/ui/input/input';
import { ForgotPasswordRequest } from '../../../../core/model/forgot-password.model';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-forgot-password-form',
  imports: [Form, Input, RouterLink, TranslocoPipe],
  templateUrl: './forgot-password-form.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ForgotPasswordForm {
  protected readonly ROUTE_URLS = ROUTE_URLS;

  private readonly authenticationService = inject(AuthenticationService);

  private readonly errors = inject(ErrorMessageService);

  private readonly model = signal<ForgotPasswordRequest>({ email: '' });

  protected readonly sent = signal(false);

  protected readonly forgotPasswordForm = form(
    this.model,
    (schema) => {
      required(schema.email, {
        message: () => this.errors.required('email'),
      });
      email(schema.email, {
        message: () => this.errors.invalid('email'),
      });
    },
    {
      submission: {
        action: async () => {
          try {
            this.sent.set(false);
            await this.authenticationService.sendForgottenPasswordEmail(this.model().email);
            this.sent.set(true);
            return undefined;
          } catch (error) {
            return {
              kind: 'forgot-password',
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
          return this.errors.invalid('email');
        case 404:
          return this.errors.accountNotFound();
        case 429:
          return this.errors.tooManyAttempts();
        default:
          return this.errors.serverError();
      }
    }
    return this.errors.serverError();
  }
}
