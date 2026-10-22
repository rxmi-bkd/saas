import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';

import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { ROUTE_URLS } from '../../core/constants/route.constants';
import { ResetPasswordForm } from './component/reset-password-form/reset-password-form';

type ResetPasswordState = 'pending' | 'reset' | 'invalid';

@Component({
  selector: 'app-reset-password',
  imports: [ResetPasswordForm, RouterLink],
  templateUrl: './reset-password.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResetPassword implements OnInit {
  protected readonly ROUTE_URLS = ROUTE_URLS;

  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly token = signal('');
  protected readonly state = signal<ResetPasswordState>('pending');

  async ngOnInit(): Promise<void> {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (token) {
      this.token.set(token);
      await this.router.navigate([], { replaceUrl: true, queryParams: {} });
    } else {
      this.state.set('invalid');
    }
  }

  protected onReset(): void {
    this.state.set('reset');
  }

  protected onInvalidToken(): void {
    this.state.set('invalid');
  }
}
