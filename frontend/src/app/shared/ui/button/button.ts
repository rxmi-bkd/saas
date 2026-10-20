import { Directive } from '@angular/core';
import { VariantStyle } from '../variant';

@Directive({
  selector: 'button[app-button], a[app-button]',
  hostDirectives: [{ directive: VariantStyle, inputs: ['variant'] }],
  host: { class: 'brutal-btn' },
})
export class Button {}
