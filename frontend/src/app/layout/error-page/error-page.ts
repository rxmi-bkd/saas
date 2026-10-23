import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Badge } from '../../shared/ui/badge/badge';
import { Button } from '../../shared/ui/button/button';
import { Card } from '../../shared/ui/card/card';
import { Variant } from '../../shared/ui/variant';
import { ROUTE_URLS } from '../../core/constants/route.constants';

@Component({
  selector: 'app-error-page',
  templateUrl: './error-page.html',
  imports: [Card, Badge, Button, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ErrorPage {
  readonly code = input.required<string>();
  readonly heading = input.required<string>();
  readonly variant = input<Variant>('primary');

  protected readonly ROUTE_URLS = ROUTE_URLS;
}
