import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';

import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

import { email, form, required } from '@angular/forms/signals';

import { AuthenticationService } from '../../../../core/service/authentication.service';
import { ROUTE_URLS } from '../../../../core/constants/route.constants';
import { ERROR_MESSAGES } from '../../../../core/constants/error-message.constants';
import { SignInRequest } from '../../../../core/model/sign-in.model';
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

  private readonly model = signal<SignInRequest>({ email: '', password: '' });

  protected readonly signInForm = form(
    this.model,
    (schema) => {
      required(schema.email, {
        message: ERROR_MESSAGES.required($localize`:@@field.email:email`),
      });
      email(schema.email, {
        message: ERROR_MESSAGES.invalid($localize`:@@field.email:email`),
      });
      required(schema.password, {
        message: ERROR_MESSAGES.required($localize`:@@field.password:password`),
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
          return ERROR_MESSAGES.invalidSignInRequest;
        case 401:
          return ERROR_MESSAGES.invalidCredentials;
        case 403:
          return ERROR_MESSAGES.disabledAccount;
        case 429:
          return ERROR_MESSAGES.tooManyAttempts;
        default:
          return ERROR_MESSAGES.serverError;
      }
    }
    return ERROR_MESSAGES.serverError;
  }
}
