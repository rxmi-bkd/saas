import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  input,
  signal,
} from '@angular/core';

import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, interval, map, Observable, Subscription, take } from 'rxjs';

import { Button } from '../button/button';
import { Variant } from '../variant';

type CooldownAction = () => Observable<boolean>;

/**
 * Button that runs an `action` when clicked, then disables itself and shows a
 * countdown (`Ns`) next to it, e.g. for "resend email" actions.
 *
 * `action` returns an Observable<boolean> saying whether it succeeded. The button
 * is disabled while the action runs. If it emits `true`, the cooldown starts and
 * the button stays disabled until the countdown reaches 0. If it emits `false` or
 * errors, no cooldown starts and the button is usable again right away. Errors are
 * not handled here: the caller should catch them inside `action` if needed.
 *
 * Inputs:
 * - `seconds` (required): cooldown length; rounded up, `<= 0` means no cooldown.
 * - `action` (required): the function to run on click.
 * - `variant`: button style, defaults to `'primary'`.
 * - `disabled`: disables the button regardless of the cooldown.
 *
 * The label is projected via `<ng-content />`.
 *
 * ```html
 * <app-cooldown-button [seconds]="60" [action]="resend">Resend</app-cooldown-button>
 * ```
 * ```ts
 * // arrow function so `this` stays bound when the button calls it
 * protected readonly resend = () => this.service.resend().pipe(map(() => true));
 * ```
 */
@Component({
  selector: 'app-cooldown-button',
  imports: [Button],
  templateUrl: './cooldown-button.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'inline-flex flex-col items-center gap-1' },
})
export class CooldownButton {
  private countdown: Subscription | undefined;
  private readonly destroyRef = inject(DestroyRef);

  readonly seconds = input.required<number>();
  readonly variant = input<Variant>('primary');
  readonly disabled = input(false);
  readonly action = input.required<CooldownAction>();

  protected readonly remaining = signal(0);
  protected readonly pending = signal(false);

  protected onClick(): void {
    this.pending.set(true);
    this.action()()
      .pipe(
        finalize(() => this.pending.set(false)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({ next: (success) => success && this.start() });
  }

  private start(): void {
    this.countdown?.unsubscribe();
    const seconds = Math.ceil(this.seconds());
    if (seconds <= 0) return;
    this.countdown = interval(1000)
      .pipe(
        take(seconds + 1),
        map((tick) => seconds - tick),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((value) => this.remaining.set(value));
  }
}
