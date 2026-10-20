import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SignUpForm } from './component/sign-up-form/sign-up-form';
import { CooldownButton } from '../../shared/ui/cooldown-button/cooldown-button';
import { of } from 'rxjs';

@Component({
  selector: 'app-sign-up',
  imports: [SignUpForm, CooldownButton],
  templateUrl: './sign-up.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SignUp {
  protected readonly test = () => Promise.resolve();
  protected readonly of = of;
}
