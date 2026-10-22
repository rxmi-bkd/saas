import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { FieldTree, FormRoot, submit } from '@angular/forms/signals';
import { from } from 'rxjs';
import { Button } from '../button/button';
import { Card } from '../card/card';
import { CooldownButton } from '../cooldown-button/cooldown-button';

@Component({
  selector: 'app-form',
  imports: [Card, FormRoot, Button, CooldownButton],
  templateUrl: './form.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Form<T> {
  readonly header = input.required<string>();
  readonly label = input.required<string>();
  readonly form = input.required<FieldTree<T>>();
  readonly cooldown = input<number>();

  protected readonly submitWithCooldown = () => from(submit(this.form()));
}
