import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { Field, FormField } from '@angular/forms/signals';

export interface SelectOption<T extends string> {
  value: T;
  label: string;
}

@Component({
  selector: 'app-select',
  templateUrl: './select.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'block' },
  imports: [FormField],
})
export class Select<T extends string = string> {
  readonly label = input<string>('');
  readonly options = input.required<readonly SelectOption<T>[]>();
  readonly formField = input.required<Field<T>>();
}
