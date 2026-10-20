import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { FieldTree, FormRoot } from '@angular/forms/signals';
import { Button } from '../button/button';
import { Card } from '../card/card';

@Component({
  selector: 'app-form',
  imports: [Card, FormRoot, Button],
  templateUrl: './form.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Form<T> {
  readonly heading = input.required<string>();
  readonly submitLabel = input.required<string>();
  readonly form = input.required<FieldTree<T>>();
}
