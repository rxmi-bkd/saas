import { inject, Injectable } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { firstValueFrom } from 'rxjs';

import { TranslocoService } from '@jsverse/transloco';

import { Language, LANGUAGES } from '../constants/language.constants';

const STORAGE_KEY = 'language';

@Injectable({ providedIn: 'root' })
export class LanguageService {
  private readonly translocoService = inject(TranslocoService);

  init() {
    const language = this.readStoredLanguage() ?? this.current();
    this.translocoService.setActiveLang(language);
    return firstValueFrom(this.translocoService.load(language));
  }

  /**
   * Active language as a signal: updates on every language change.
   * Use it where the code must re-run on a switch: templates (`date: ... : lang()`),
   * computed(), effect(), form validators.
   */
  readonly lang = toSignal(this.translocoService.langChanges$, { initialValue: this.translocoService.getActiveLang() });

  /**
   * Active language read once, NOT reactive: a template or computed() using it
   * won't update on a language switch. Use it in event handlers, submit actions
   * or one-off initialisation. Prefer `lang()` when unsure.
   */
  current(): Language {
    return this.translocoService.getActiveLang() as Language;
  }

  translate(key: string, params?: Record<string, unknown>): string {
    this.lang();
    return this.translocoService.translate(key, params);
  }

  async use(language: Language): Promise<void> {
    await firstValueFrom(this.translocoService.load(language));
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
