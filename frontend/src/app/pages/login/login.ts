import {
  ChangeDetectionStrategy,
  Component,
  inject,
  signal,
} from '@angular/core';

import { Router } from '@angular/router';

import {
  email,
  form,
  FormField,
  FormRoot,
  required,
} from '@angular/forms/signals';

import { AuthenticationService } from '../../core/service/authentication.service';
import { ROUTE_URLS } from '../../core/router/route.constants';
import { LoginForm } from '../../core/model/login.model';

@Component({
  selector: 'app-login',
  imports: [FormRoot, FormField],
  templateUrl: './login.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Login {
  private readonly router = inject(Router);
  private readonly authenticationService = inject(AuthenticationService);

  private readonly model = signal<LoginForm>({ email: '', password: '' });

  protected readonly loginForm = form(
    this.model,
    (path) => {
      required(path.email, { message: 'Email is required' });
      email(path.email, { message: 'Email is invalid' });
      required(path.password, { message: 'Password is required' });
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
            return { kind: 'login', message: 'Invalid email or password' };
          }
        },
      },
    },
  );
}
