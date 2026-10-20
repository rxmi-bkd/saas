export type Variant = 'primary' | 'secondary' | 'accent' | 'danger';

const VARIANT_CLASSES: Record<Variant, string> = {
  primary: 'bg-main',
  secondary: 'bg-white',
  accent: 'bg-accent',
  danger: 'bg-danger',
};

export function getVariantClass(variant: Variant) {
  return VARIANT_CLASSES[variant];
}
