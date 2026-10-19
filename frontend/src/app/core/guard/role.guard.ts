import { inject } from '@angular/core';
import { AuthenticationService } from '../service/authentication.service';
import { Router } from '@angular/router';
import { Role } from '../model/user.model';
import { ROUTE_URLS } from '../router/route.constants';

function roleGuard(role: Role) {
  const router = inject(Router);
  const authenticationService = inject(AuthenticationService);
  return authenticationService.user()?.role === role
    ? true
    : router.createUrlTree([ROUTE_URLS.accessDenied]);
}

export function adminGuard() {
  return roleGuard('ROLE_ADMIN');
}
