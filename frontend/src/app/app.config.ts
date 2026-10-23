import { provideHttpClient, withXsrfConfiguration } from '@angular/common/http';

import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import {
  ApplicationConfig,
  inject,
  isDevMode,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';

import { provideRouter } from '@angular/router';

import { provideTransloco } from '@jsverse/transloco';

import { routes } from './app.routes';
import { AuthenticationService } from './core/service/authentication.service';
import { TranslocoHttpLoader } from './core/service/transloco-loader';
import { LANGUAGES } from './core/constants/language.constants';

registerLocaleData(localeFr);

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
    provideTransloco({
      config: {
        availableLangs: [...LANGUAGES],
        defaultLang: 'en',
        fallbackLang: 'en',
        reRenderOnLangChange: true,
        prodMode: !isDevMode(),
      },
      loader: TranslocoHttpLoader,
    }),
    provideAppInitializer(() => inject(AuthenticationService).refresh()),
  ],
};
