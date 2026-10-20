import { Directive } from '@angular/core';
import { VariantStyle } from '../variant';

@Directive({
  selector: 'span[app-badge]',
  hostDirectives: [{ directive: VariantStyle, inputs: ['variant'] }],
  host: {
    class: 'inline-block border-2 border-ink px-2 py-1 text-sm font-bold shadow-brutal',
  },
})
export class Badge {}
