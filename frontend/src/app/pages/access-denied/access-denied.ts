import { ChangeDetectionStrategy, Component } from '@angular/core';
import { StatusPage } from '../../shared/ui/status-page/status-page';

@Component({
  selector: 'app-access-denied',
  templateUrl: './access-denied.html',
  imports: [StatusPage],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccessDenied {}
