import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { User } from '../model/user.model';
import { UserHttp } from '../http/user.http';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  private readonly userHttpService = inject(UserHttp);

  private readonly _user = signal<User | null>(null);

  readonly user = this._user.asReadonly();
  readonly isAuthenticated = computed(() => this._user() != null);

  async refresh(): Promise<void> {
    try {
      const user = await firstValueFrom(this.userHttpService.me());
      this._user.set(user);
    } catch {
      this._user.set(null);
    }
  }
}
