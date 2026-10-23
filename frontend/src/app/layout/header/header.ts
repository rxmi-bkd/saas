import { ChangeDetectionStrategy, Component, computed, effect, inject, linkedSignal } from '@angular/core';
import { form } from '@angular/forms/signals';
import { RouterLink } from '@angular/router';
import { AuthenticationService } from '../../core/service/authentication.service';
import { Language, LanguageService } from '../../core/service/language.service';
import { ROUTE_URLS } from '../../core/constants/route.constants';
import { Select, SelectOption } from '../../shared/ui/select/select';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-header',
  imports: [RouterLink, TranslocoPipe, Select],
  templateUrl: './header.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header {
  private readonly languageService = inject(LanguageService);
  private readonly authenticationService = inject(AuthenticationService);

  protected readonly language = this.languageService.current;
  protected readonly languages = this.languageService.languages;
  protected readonly languageOptions = computed<SelectOption<Language>[]>(() =>
    this.languages.map((lang) => ({ value: lang, label: lang.toUpperCase() })),
  );
  protected readonly languageForm = form(linkedSignal<Language>(() => this.language()));
  protected readonly isAdmin = this.authenticationService.isAdmin;
  protected readonly isAuthenticated = this.authenticationService.isAuthenticated;

  protected readonly ROUTE_URLS = ROUTE_URLS;

  constructor() {
    effect(() => {
      const language = this.languageForm().value();
      if (language !== this.language()) {
        void this.languageService.use(language);
      }
    });
  }

  protected async logout(): Promise<void> {
    await this.authenticationService.logout();
  }
}
