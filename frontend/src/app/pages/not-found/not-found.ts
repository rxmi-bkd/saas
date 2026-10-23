import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ErrorPage } from '../../layout/error-page/error-page';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-not-found',
  templateUrl: './not-found.html',
  imports: [ErrorPage, TranslocoPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotFound {}
