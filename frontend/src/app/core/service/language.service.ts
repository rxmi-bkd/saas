import { inject, Injectable } from '@angular/core';

import { TranslocoService } from '@jsverse/transloco';

import { Language, LANGUAGES } from '../constants/language.constants';

const STORAGE_KEY = 'language';

@Injectable({ providedIn: 'root' })
export class LanguageService {
  private readonly translocoService = inject(TranslocoService);

  init(): void {
    const stored = this.readStoredLanguage();
    if (stored) this.translocoService.setActiveLang(stored);
  }

  use(language: Language): void {
    this.translocoService.setActiveLang(language);
    this.storeLanguage(language);
  }

  private readStoredLanguage(): Language | null {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      return this.isLanguage(stored) ? stored : null;
    } catch {
      // storage unavailable (private mode, blocked): fall back to the default language
      return null;
    }
  }

  private storeLanguage(language: Language): void {
    try {
      localStorage.setItem(STORAGE_KEY, language);
    } catch {
      // storage unavailable: the choice just isn't remembered
    }
  }

  private isLanguage(value: string | null): value is Language {
    return LANGUAGES.some((language) => language === value);
  }
}
