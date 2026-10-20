import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { FieldTree, FormRoot } from '@angular/forms/signals';
import { Button } from './button';
import { Card } from './card';

/**
 * Card + title + signal form + form-level errors + submit button.
 * The page keeps the model, validation and submit action; this only renders.
 *
 *   <app-form heading="Login" submitLabel="Log in" [form]="loginForm">
 *     <app-text-field label="Email" [formField]="loginForm.email" />
 *   </app-form>
 */
@Component({
  selector: 'app-form',
  imports: [Card, Button, FormRoot],
  template: `
    <app-card>
      <h1 class="mb-6">{{ heading() }}</h1>

      <form [formRoot]="form()" class="flex flex-col gap-6">
        <ng-content />

        @for (error of form()().errors(); track error.kind) {
          <p class="brutal-error">{{ error.message }}</p>
        }

        <button variant="primary" app-button [disabled]="form()().submitting()">
          {{ submitLabel() }}
        </button>
      </form>
    </app-card>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Form<T> {
  readonly heading = input.required<string>();
  readonly submitLabel = input.required<string>();
  readonly form = input.required<FieldTree<T>>();
}
