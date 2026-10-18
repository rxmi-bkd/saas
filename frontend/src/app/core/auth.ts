import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';

export interface User {
  id: string;
  email: string;
  role: string;
  createdAt: string;
  enabled: boolean;
}

const API = '/api/v1';

@Injectable({ providedIn: 'root' })
export class Auth {
  private readonly http = inject(HttpClient);

  private readonly _user = signal<User | null>(null);
  readonly user = this._user.asReadonly();
  readonly isAuthenticated = computed(() => this._user() !== null);

  /** Restores the session (if any) from the backend. Never rejects. */
  async loadCurrentUser(): Promise<void> {
    try {
      this._user.set(await firstValueFrom(this.http.get<User>(`${API}/users/me`)));
    } catch {
      this._user.set(null);
    }
  }

  async signIn(email: string, password: string): Promise<void> {
    this._user.set(await firstValueFrom(this.http.post<User>(`${API}/public/security/login`, { email, password })));
  }

  async signUp(email: string, password: string): Promise<void> {
    await firstValueFrom(this.http.post<User>(`${API}/public/users`, { email, password }));
  }

  async signOut(): Promise<void> {
    try {
      await firstValueFrom(this.http.post<void>(`${API}/public/security/logout`, null));
    } finally {
      this._user.set(null);
    }
  }

  async sendForgottenPasswordEmail(email: string): Promise<void> {
    await firstValueFrom(this.http.post<void>(`${API}/public/security/send-forgotten-password-email`, { email }));
  }
}
