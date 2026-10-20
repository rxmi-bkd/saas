import { computed, Directive, input } from '@angular/core';
import { Variant, VARIANT_CLASSES } from '../variants';

@Directive({
  selector: 'span[app-badge]',
  host: {
    class: 'inline-block border-2 border-ink px-2 py-1 text-sm font-bold shadow-brutal',
    '[class]': 'variantClass()',
  },
})
export class Badge {
  readonly variant = input<Variant>('primary');

  protected readonly variantClass = computed(
    () => VARIANT_CLASSES[this.variant()],
  );
}
