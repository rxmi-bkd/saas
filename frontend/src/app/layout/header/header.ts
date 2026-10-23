import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthenticationService } from '../../core/service/authentication.service';
import { Language, LanguageService } from '../../core/service/language.service';
import { ROUTE_URLS } from '../../core/constants/route.constants';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-header',
  imports: [RouterLink, TranslocoPipe],
  templateUrl: './header.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header {
  private readonly authenticationService = inject(AuthenticationService);

  protected readonly isAuthenticated = this.authenticationService.isAuthenticated;

  protected readonly isAdmin = this.authenticationService.isAdmin;

  private readonly languageService = inject(LanguageService);

  protected readonly language = this.languageService.current;
  protected readonly languages = this.languageService.languages;

  protected readonly ROUTE_URLS = ROUTE_URLS;

  protected async setLanguage(language: Language): Promise<void> {
    await this.languageService.use(language);
  }

  protected async logout(): Promise<void> {
    await this.authenticationService.logout();
  }
}
