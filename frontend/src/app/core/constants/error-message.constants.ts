export const ERROR_MESSAGES = {
  required: (field: string) => `${field} is required`,
  invalid: (field: string) => `${field} is invalid`,
  invalidCredentials: 'Invalid email or password',
  invalidSignInRequest: 'Please check your email and password',
  disabledAccount: 'Your account is disabled or not yet activated',
  tooManyAttempts: 'Too many failed attempts, please try again later',
  serverError: 'Something went wrong on our side, please try again later',
  networkError: 'Unable to reach the server, please check your connection',
  forgotPasswordFailed: 'Unable to send the reset email, please try again',
  invalidSignUpRequest: 'Please check your email and password',
  emailAlreadyUsed: 'An account with this email already exists',
};

