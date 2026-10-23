import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';

import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

import { email, form, maxLength, minLength, required } from '@angular/forms/signals';

import { UserService } from '../../../../core/service/user.service';
import { ROUTE_URLS } from '../../../../core/constants/route.constants';
import { PASSWORD_MAX_LENGTH, PASSWORD_MIN_LENGTH } from '../../../../core/constants/password.constants';
import { ERROR_MESSAGES } from '../../../../core/constants/error-message.constants';
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
        message: ERROR_MESSAGES.required($localize`:@@field.email:email`),
      });
      email(schema.email, {
        message: ERROR_MESSAGES.invalid($localize`:@@field.email:email`),
      });
      required(schema.password, {
        message: ERROR_MESSAGES.required($localize`:@@field.password:password`),
      });
      minLength(schema.password, PASSWORD_MIN_LENGTH, {
        message: ERROR_MESSAGES.minLength($localize`:@@field.password:password`, PASSWORD_MIN_LENGTH),
      });
      maxLength(schema.password, PASSWORD_MAX_LENGTH, {
        message: ERROR_MESSAGES.maxLength($localize`:@@field.password:password`, PASSWORD_MAX_LENGTH),
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
          } catch (error) {
            return {
              kind: 'sign-up',
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
          return ERROR_MESSAGES.invalidSignUpRequest;
        case 409:
          return ERROR_MESSAGES.emailAlreadyUsed;
        case 429:
          return ERROR_MESSAGES.tooManyAttempts;
        default:
          return ERROR_MESSAGES.serverError;
      }
    }
    return ERROR_MESSAGES.serverError;
  }
}
