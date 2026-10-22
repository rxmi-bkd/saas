import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, signal } from '@angular/core';

import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, interval, map, Observable, Subscription, take } from 'rxjs';

import { Button } from '../button/button';
import { Variant } from '../variant';

const MIN_SECONDS = 10;

type CooldownAction = () => Observable<boolean>;

/**
 * Button that runs an `action` when clicked, then disables itself and shows a
 * countdown next to it, e.g. for "resend email" actions.
 *
 * `action` returns an Observable<boolean> saying whether it succeeded. The button
 * is disabled while the action runs. If it emits `true`, the cooldown starts and
 * the button stays disabled until the countdown reaches 0. If it emits `false` or
 * errors, no cooldown starts and the button is usable again right away. Errors are
 * silently ignored here: the caller should handle them inside `action` (e.g. with
 * `catchError`) if it needs to report them.
 *
 * Inputs:
 * - `seconds` (required): cooldown length; rounded up, and raised to 10 if smaller
 *   (or not a valid number).
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
      .subscribe({
        next: (success) => {
          if (success) this.start();
        },
        error: () => {},
      });
  }

  private start(): void {
    this.countdown?.unsubscribe();
    const seconds = Math.ceil(Math.max(this.seconds(), MIN_SECONDS));
    this.remaining.set(seconds);
    this.countdown = interval(1000)
      .pipe(
        take(seconds),
        map((tick) => seconds - tick - 1),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((value) => this.remaining.set(value));
  }
}
