import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ErrorPage } from '../../layout/error-page/error-page';

@Component({
  selector: 'app-access-denied',
  templateUrl: './access-denied.html',
  imports: [ErrorPage],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccessDenied {}
