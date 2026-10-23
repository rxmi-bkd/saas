import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ROUTE_URLS } from '../../core/constants/route.constants';
import { StatusMessage } from '../../shared/ui/status-message/status-message';

@Component({
  selector: 'app-password-updated',
  imports: [StatusMessage],
  templateUrl: './password-updated.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PasswordUpdated {
  protected readonly ROUTE_URLS = ROUTE_URLS;
}
