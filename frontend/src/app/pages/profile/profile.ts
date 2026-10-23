import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { LanguageService } from '../../core/service/language.service';
import { AuthenticationService } from '../../core/service/authentication.service';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-profile',
  imports: [DatePipe, TranslocoPipe],
  templateUrl: './profile.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Profile {
  private readonly authenticationService = inject(AuthenticationService);
  private readonly languageService = inject(LanguageService);

  protected readonly language = this.languageService.current;
  protected readonly user = this.authenticationService.user;
}
