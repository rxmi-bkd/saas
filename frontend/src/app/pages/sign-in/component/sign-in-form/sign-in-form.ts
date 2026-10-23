import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';

import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

import { email, form, required } from '@angular/forms/signals';

import { AuthenticationService } from '../../../../core/service/authentication.service';
import { ROUTE_URLS } from '../../../../core/constants/route.constants';
import { ErrorMessageService } from '../../../../core/service/error-message.service';
import { SignInRequest } from '../../../../core/model/sign-in.model';
import { Form } from '../../../../shared/ui/form/form';
import { Input } from '../../../../shared/ui/input/input';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-sign-in-form',
  imports: [Form, Input, RouterLink, TranslocoPipe],
  templateUrl: './sign-in-form.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SignInForm {
  protected readonly ROUTE_URLS = ROUTE_URLS;

  private readonly router = inject(Router);
  private readonly authenticationService = inject(AuthenticationService);

  private readonly errors = inject(ErrorMessageService);

  private readonly model = signal<SignInRequest>({ email: '', password: '' });

  protected readonly signInForm = form(
    this.model,
    (schema) => {
      required(schema.email, {
        message: () => this.errors.required('email'),
      });
      email(schema.email, {
        message: () => this.errors.invalid('email'),
      });
      required(schema.password, {
        message: () => this.errors.required('password'),
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
          } catch (error) {
            return {
              kind: 'sign-in',
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
          return this.errors.invalidSignInRequest();
        case 401:
          return this.errors.invalidCredentials();
        case 403:
          return this.errors.disabledAccount();
        case 429:
          return this.errors.tooManyAttempts();
        default:
          return this.errors.serverError();
      }
    }
    return this.errors.serverError();
  }
}
