import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Badge } from '../badge/badge';
import { Button } from '../button/button';
import { Card } from '../card/card';
import { Variant } from '../variants';
import { ROUTE_URLS } from '../../../core/constants/route.constants';

@Component({
  selector: 'app-status-page',
  templateUrl: './status-page.html',
  imports: [Card, Badge, Button, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StatusPage {
  readonly code = input.required<string>();
  readonly heading = input.required<string>();
  readonly variant = input<Variant>('primary');

  protected readonly ROUTE_URLS = ROUTE_URLS;
}
