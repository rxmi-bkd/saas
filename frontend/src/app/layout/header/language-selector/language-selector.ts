import { ChangeDetectionStrategy, Component, effect, inject, signal } from '@angular/core';

import { form } from '@angular/forms/signals';

import { Language, LANGUAGES } from '../../../core/constants/language.constants';
import { LanguageForm } from '../../../core/model/language.model';
import { Select } from '../../../shared/ui/select/select';
import { TranslocoService } from '@jsverse/transloco';
import { LanguageService } from '../../../core/service/language.service';

const LANGUAGE_LABELS: Record<Language, string> = {
  en: 'English',
  fr: 'Français',
};

@Component({
  selector: 'app-language-selector',
  imports: [Select],
  templateUrl: './language-selector.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LanguageSelector {
  private readonly languageService = inject(LanguageService);
  private readonly translocoService = inject(TranslocoService);

  protected readonly options = LANGUAGES.map((language) => ({
    value: language as Language,
    label: LANGUAGE_LABELS[language as Language],
  }));

  private readonly model = signal<LanguageForm>({ language: this.translocoService.getActiveLang() as Language });

  protected readonly languageForm = form(this.model);

  constructor() {
    effect(() => {
      const { language } = this.model();
      if (language !== this.translocoService.getActiveLang()) {
        this.languageService.use(language);
      }
    });
  }
}
