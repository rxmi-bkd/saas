import { inject, Injectable } from '@angular/core';

import { LanguageService } from './language.service';

type FieldName = 'email' | 'password';

@Injectable({ providedIn: 'root' })
export class ErrorMessageService {
  private readonly languageService = inject(LanguageService);

  required = (field: FieldName) => this.fieldError('required', field);
  invalid = (field: FieldName) => this.fieldError('invalid', field);
  minLength = (field: FieldName, length: number) => this.fieldError('minLength', field, length);
  maxLength = (field: FieldName, length: number) => this.fieldError('maxLength', field, length);

  invalidCredentials = () => this.translate('invalidCredentials');
  invalidSignInRequest = () => this.translate('invalidSignInRequest');
  disabledAccount = () => this.translate('disabledAccount');
  tooManyAttempts = () => this.translate('tooManyAttempts');
  serverError = () => this.translate('serverError');
  networkError = () => this.translate('networkError');
  accountNotFound = () => this.translate('accountNotFound');
  forgotPasswordFailed = () => this.translate('forgotPasswordFailed');
  invalidResetPasswordRequest = () => this.translate('invalidResetPasswordRequest');
  invalidResetToken = () => this.translate('invalidResetToken');
  invalidSignUpRequest = () => this.translate('invalidSignUpRequest');
  emailAlreadyUsed = () => this.translate('emailAlreadyUsed');

  private fieldError(key: string, field: FieldName, length?: number): string {
    return this.translate(key, { field: this.languageService.translate(`field.${field}`), length });
  }

  private translate(key: string, params?: Record<string, unknown>): string {
    return this.languageService.translate(`error.${key}`, params);
  }
}
