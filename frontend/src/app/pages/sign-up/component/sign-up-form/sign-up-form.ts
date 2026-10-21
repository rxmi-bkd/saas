import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';

import { Router, RouterLink } from '@angular/router';

import { email, form, required } from '@angular/forms/signals';

import { UserService } from '../../../../core/service/user.service';
import { ROUTE_URLS } from '../../../../core/constants/route.constants';
import { ERROR_MESSAGES, FIELD_NAMES } from '../../../../core/constants/error-message.constants';
import { SignUpRequest } from '../../../../core/model/sign-up.model';
import { Form } from '../../../../shared/ui/form/form';
import { Input } from '../../../../shared/ui/input/input';

@Component({
  selector: 'app-sign-up-form',
  imports: [Form, Input, RouterLink],
  templateUrl: './sign-up-form.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SignUpForm {
  protected readonly ROUTE_URLS = ROUTE_URLS;

  private readonly router = inject(Router);
  private readonly userService = inject(UserService);

  private readonly model = signal<SignUpRequest>({ email: '', password: '' });

  protected readonly signUpForm = form(
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
            await this.userService.createUser(email, password);
            await this.router.navigateByUrl(ROUTE_URLS.enableAccount);
            return undefined;
          } catch {
            return {
              kind: 'sign-up',
              message: ERROR_MESSAGES.signUpFailed,
            };
          }
        },
      },
    },
  );
}
