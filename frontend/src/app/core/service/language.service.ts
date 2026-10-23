import { DOCUMENT } from '@angular/common';
import { inject, Injectable } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { TranslocoService } from '@jsverse/transloco';
import { firstValueFrom, Observable } from 'rxjs';

export const LANGUAGES = ['en', 'fr'] as const;
export type Language = (typeof LANGUAGES)[number];

const DEFAULT_LANGUAGE: Language = 'en';
const STORAGE_KEY = 'language';

@Injectable({ providedIn: 'root' })
export class LanguageService {
  private readonly transloco = inject(TranslocoService);
  private readonly document = inject(DOCUMENT);

  readonly languages = LANGUAGES;
  readonly current = toSignal(this.transloco.langChanges$ as Observable<Language>, { requireSync: true });

  async init(): Promise<void> {
    await this.use(this.initialLanguage());
  }

  async use(language: Language): Promise<void> {
    await firstValueFrom(this.transloco.load(language));
    this.transloco.setActiveLang(language);
    this.document.documentElement.lang = language;
    try {
      localStorage.setItem(STORAGE_KEY, language);
    } catch {
      // storage unavailable (private mode, blocked): the choice just isn't remembered
    }
  }

  private initialLanguage(): Language {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (this.isLanguage(stored)) return stored;
    } catch {
      // storage unavailable: fall through to the browser language
    }
    const browser = navigator.language.split('-')[0];
    return this.isLanguage(browser) ? browser : DEFAULT_LANGUAGE;
  }

  private isLanguage(value: string | null): value is Language {
    return LANGUAGES.some((language) => language === value);
  }
}
