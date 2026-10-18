import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../model/user.model';
import { PUBLIC_API } from './api.constants';

@Injectable({ providedIn: 'root' })
export class SecurityHttpService {
  private readonly http = inject(HttpClient);

  login(email: string, password: string): Observable<User> {
    return this.http.post<User>(`${PUBLIC_API}/security/login`, { email, password });
  }

  enableUser(jwt: string): Observable<void> {
    return this.http.post<void>(`${PUBLIC_API}/enable-user`, { jwt });
  }

  sendActivationEmail(email: string): Observable<void> {
    return this.http.post<void>(`${PUBLIC_API}/security/send-activation-email`, { email });
  }

  sendForgottenPasswordEmail(email: string): Observable<void> {
    return this.http.post<void>(`${PUBLIC_API}/security/send-forgotten-password-email`, { email });
  }

  resetPassword(jwt: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${PUBLIC_API}/security/reset-password`, { jwt, newPassword });
  }
}
