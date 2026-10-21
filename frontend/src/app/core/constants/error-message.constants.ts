export const ERROR_MESSAGES = {
  required: (field: string) => `${field} is required`,
  invalid: (field: string) => `${field} is invalid`,
  invalidCredentials: 'Invalid email or password',
  forgotPasswordFailed: 'Unable to send the reset email, please try again',
  signUpFailed: 'Unable to create your account',
};

export const FIELD_NAMES = {
  email: 'Email',
  password: 'Password',
};
