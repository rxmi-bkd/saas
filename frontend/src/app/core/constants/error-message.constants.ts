export const ERROR_MESSAGES = {
  required: (field: string) => $localize`:@@error.required:${field}:field: is required`,
  invalid: (field: string) => $localize`:@@error.invalid:${field}:field: is invalid`,
  minLength: (field: string, length: number) =>
    $localize`:@@error.minLength:${field}:field: must be at least ${length}:length: characters`,
  maxLength: (field: string, length: number) =>
    $localize`:@@error.maxLength:${field}:field: must be at most ${length}:length: characters`,
  invalidCredentials: $localize`:@@error.invalidCredentials:Invalid email or password`,
  invalidSignInRequest: $localize`:@@error.invalidSignInRequest:Please check your email and password`,
  disabledAccount: $localize`:@@error.disabledAccount:Your account is disabled or not yet activated`,
  tooManyAttempts: $localize`:@@error.tooManyAttempts:Too many failed attempts, please try again later`,
  serverError: $localize`:@@error.serverError:Something went wrong on our side, please try again later`,
  networkError: $localize`:@@error.networkError:Unable to reach the server, please check your connection`,
  accountNotFound: $localize`:@@error.accountNotFound:No account found for this email`,
  forgotPasswordFailed: $localize`:@@error.forgotPasswordFailed:Unable to send the reset email, please try again`,
  invalidResetPasswordRequest: $localize`:@@error.invalidResetPasswordRequest:Please check your new password`,
  invalidResetToken: $localize`:@@error.invalidResetToken:This reset link is invalid or has expired`,
  invalidSignUpRequest: $localize`:@@error.invalidSignUpRequest:Please check your email and password`,
  emailAlreadyUsed: $localize`:@@error.emailAlreadyUsed:An account with this email already exists`,
};
