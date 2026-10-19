import { computed, inject, Injectable, signal } from '@angular/core';
import { User } from '../model/user.model';
import { UserHttpService } from '../http/user-http.service';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  private readonly userHttpService = inject(UserHttpService);

  private readonly _user = signal<User | null>(null);
  private readonly _isLoading = signal(true);

  readonly user = this._user.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly isAuthenticated = computed(() => this._user() != null);

  constructor() {
    this.refresh();
  }

  refresh(): void {
    this._isLoading.set(true);
    this.userHttpService.me().subscribe({
      next: (user) => {
        this._user.set(user);
        this._isLoading.set(false);
      },
      error: () => {
        this._user.set(null);
        this._isLoading.set(false);
      },
    });
  }
}
