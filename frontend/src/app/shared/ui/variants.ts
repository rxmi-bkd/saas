export type Variant = 'primary' | 'secondary' | 'accent' | 'danger';

export const VARIANT_CLASSES: Record<Variant, string> = {
  primary: 'bg-main',
  secondary: 'bg-white',
  accent: 'bg-accent',
  danger: 'bg-danger',
};
