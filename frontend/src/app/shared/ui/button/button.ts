import { computed, Directive, input } from '@angular/core';
import { Variant, VARIANT_CLASSES } from '../variants';

@Directive({
  selector: 'button[app-button], a[app-button]',
  host: {
    class: 'brutal-btn',
    '[class]': 'variantClass()',
  },
})
export class Button {
  readonly variant = input<Variant>('primary');

  protected readonly variantClass = computed(
    () => VARIANT_CLASSES[this.variant()],
  );
}
