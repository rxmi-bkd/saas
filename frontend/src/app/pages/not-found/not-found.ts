import { ChangeDetectionStrategy, Component } from '@angular/core';
import { StatusPage } from '../../shared/ui/status-page/status-page';

@Component({
  selector: 'app-not-found',
  templateUrl: './not-found.html',
  imports: [StatusPage],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotFound {}
