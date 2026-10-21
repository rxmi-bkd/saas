import { provideHttpClient, withXsrfConfiguration } from '@angular/common/http';

import { ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners } from '@angular/core';

import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { AuthenticationService } from './core/service/authentication.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(
      withXsrfConfiguration({
        cookieName: 'XSRF-TOKEN',
        headerName: 'X-XSRF-TOKEN',
      }),
    ),
    provideAppInitializer(() => inject(AuthenticationService).refresh()),
  ],
};
