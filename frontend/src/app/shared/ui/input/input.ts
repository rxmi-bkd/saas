import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { Field, FormField } from '@angular/forms/signals';

interface InputValueType {
  text: string;
  email: string;
  password: string;
  url: string;
  number: number;
}

type InputType = keyof InputValueType;

@Component({
  selector: 'app-input',
  templateUrl: './input.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'block' },
  imports: [FormField],
})
export class Input<K extends InputType = 'text'> {
  readonly label = input.required<string>();
  readonly type = input<K>('text' as K);
  readonly placeholder = input<string>('');
  readonly formField = input.required<Field<InputValueType[K]>>();
}
