import { computed, Directive, input } from '@angular/core';

export type Variant = 'primary' | 'secondary' | 'accent' | 'danger';

const VARIANT_CLASSES: Record<Variant, string> = {
  primary: 'bg-main',
  secondary: 'bg-white',
  accent: 'bg-accent',
  danger: 'bg-danger',
};

@Directive({
  host: { '[class]': 'variantClass()' },
})
export class VariantStyle {
  readonly variant = input<Variant>('primary');

  protected readonly variantClass = computed(() => VARIANT_CLASSES[this.variant()]);
}
