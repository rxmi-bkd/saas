import {
  ChangeDetectionStrategy,
  Component,
  input,
  model,
  output,
} from '@angular/core';

import { FormValueControl, ValidationError } from '@angular/forms/signals';

type InputType = 'text' | 'email' | 'password' | 'search' | 'tel' | 'url';

@Component({
  selector: 'app-text-field',
  template: `
    <label class="brutal-label">
      {{ label() }}
      <input
        class="brutal-input"
        [type]="type()"
        [attr.autocomplete]="autocomplete()"
        [attr.placeholder]="placeholder()"
        [disabled]="disabled()"
        [value]="value()"
        (input)="value.set($any($event.target).value)"
        (blur)="touch.emit()"
      />
      @if (touched()) {
        @for (error of errors(); track error.kind) {
          <span class="brutal-error">{{ error.message }}</span>
        }
      }
    </label>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'block' },
})
export class TextField implements FormValueControl<string> {
  readonly label = input.required<string>();
  readonly type = input<InputType>('text');
  readonly autocomplete = input<string>();
  readonly placeholder = input<string>();

  // FormValueControl contract (kept in sync by the formField directive)
  readonly value = model('');
  readonly errors = input<readonly ValidationError.WithOptionalFieldTree[]>([]);
  readonly touched = input(false);
  readonly disabled = input(false);
  readonly touch = output<void>();
}
