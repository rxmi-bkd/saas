import {
  ChangeDetectionStrategy,
  Component,
  computed,
  input,
} from '@angular/core';
import { getVariantClass, Variant } from './variants';

@Component({
  selector: 'button[app-button], a[app-button]',
  template: `<ng-content />`,
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    class: 'brutal-btn',
    '[class]': 'variantClass()',
  },
})
export class Button {
  readonly variant = input.required<Variant>();

  protected readonly variantClass = computed(() =>
    getVariantClass(this.variant()),
  );
}
