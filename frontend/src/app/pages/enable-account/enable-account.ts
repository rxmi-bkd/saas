import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ROUTE_URLS } from '../../core/constants/route.constants';

@Component({
  selector: 'app-enable-account',
  imports: [RouterLink],
  templateUrl: './enable-account.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EnableAccount {
  protected readonly ROUTE_URLS = ROUTE_URLS;
}
