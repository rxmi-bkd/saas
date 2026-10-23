import { inject, Injectable } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { TranslocoService } from '@jsverse/transloco';

type FieldName = 'email' | 'password';

/**
 * User-facing error messages, translated with Transloco.
 *
 * Each method reads the active language, so when it is called from a reactive
 * context (e.g. a signal-forms validator `message` function) the result is
 * recomputed when the language changes.
 */
@Injectable({ providedIn: 'root' })
export class ErrorMessageService {
  private readonly transloco = inject(TranslocoService);
  private readonly lang = toSignal(this.transloco.langChanges$, { requireSync: true });

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
    return this.translate(key, { field: this.transloco.translate(`field.${field}`), length });
  }

  private translate(key: string, params?: Record<string, unknown>): string {
    this.lang();
    return this.transloco.translate(`error.${key}`, params);
  }
}
