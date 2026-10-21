import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';

import { RouterLink } from '@angular/router';

import { email, form, required } from '@angular/forms/signals';

import { AuthenticationService } from '../../../../core/service/authentication.service';
import { ROUTE_URLS } from '../../../../core/constants/route.constants';
import { ERROR_MESSAGES, FIELD_NAMES } from '../../../../core/constants/error-message.constants';
import { Form } from '../../../../shared/ui/form/form';
import { Input } from '../../../../shared/ui/input/input';
import { ForgotPasswordRequest } from '../../../../core/model/forgot-password.model';

@Component({
  selector: 'app-forgot-password-form',
  imports: [Form, Input, RouterLink],
  templateUrl: './forgot-password-form.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ForgotPasswordForm {
  protected readonly ROUTE_URLS = ROUTE_URLS;

  private readonly authenticationService = inject(AuthenticationService);

  private readonly model = signal<ForgotPasswordRequest>({ email: '' });

  protected readonly sent = signal(false);

  protected readonly forgotPasswordForm = form(
    this.model,
    (schema) => {
      required(schema.email, {
        message: ERROR_MESSAGES.required(FIELD_NAMES.email),
      });
      email(schema.email, {
        message: ERROR_MESSAGES.invalid(FIELD_NAMES.email),
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
          } catch {
            this.sent.set(true);
            return undefined;
          }
        },
      },
    },
  );
}
