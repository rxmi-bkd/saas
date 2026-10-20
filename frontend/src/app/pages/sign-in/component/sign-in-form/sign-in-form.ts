import {
  ChangeDetectionStrategy,
  Component,
  inject,
  signal,
} from '@angular/core';

import { Router, RouterLink } from '@angular/router';

import { email, form, required } from '@angular/forms/signals';

import { AuthenticationService } from '../../../../core/service/authentication.service';
import { ROUTE_URLS } from '../../../../core/constants/route.constants';
import {
  ERROR_MESSAGES,
  FIELD_NAMES,
} from '../../../../core/constants/error-message.constants';
import { SignInFormModel } from '../../../../core/model/sign-in.model';
import { Form } from '../../../../shared/ui/form/form';
import { Input } from '../../../../shared/ui/input/input';

@Component({
  selector: 'app-sign-in-form',
  imports: [Form, Input, RouterLink],
  templateUrl: './sign-in-form.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SignInForm {
  protected readonly ROUTE_URLS = ROUTE_URLS;

  private readonly router = inject(Router);
  private readonly authenticationService = inject(AuthenticationService);

  private readonly model = signal<SignInFormModel>({ email: '', password: '' });

  protected readonly signInForm = form(
    this.model,
    (schema) => {
      required(schema.email, {
        message: ERROR_MESSAGES.required(FIELD_NAMES.email),
      });
      email(schema.email, {
        message: ERROR_MESSAGES.invalid(FIELD_NAMES.email),
      });
      required(schema.password, {
        message: ERROR_MESSAGES.required(FIELD_NAMES.password),
      });
    },
    {
      submission: {
        action: async () => {
          const { email, password } = this.model();
          try {
            await this.authenticationService.login(email, password);
            await this.router.navigateByUrl(ROUTE_URLS.home);
            return undefined;
          } catch {
            return {
              kind: 'sign-in',
              message: ERROR_MESSAGES.invalidCredentials,
            };
          }
        },
      },
    },
  );
}
