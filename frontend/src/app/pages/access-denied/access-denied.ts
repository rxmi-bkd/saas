import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ErrorPage } from '../../layout/error-page/error-page';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-access-denied',
  templateUrl: './access-denied.html',
  imports: [ErrorPage, TranslocoPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccessDenied {}
