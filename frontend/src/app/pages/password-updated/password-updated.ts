import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ROUTE_URLS } from '../../core/constants/route.constants';
import { StatusMessage } from '../../shared/ui/status-message/status-message';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-password-updated',
  imports: [StatusMessage, TranslocoPipe],
  templateUrl: './password-updated.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PasswordUpdated {
  protected readonly ROUTE_URLS = ROUTE_URLS;
}
