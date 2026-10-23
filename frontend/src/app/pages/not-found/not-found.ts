import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ErrorPage } from '../../layout/error-page/error-page';

@Component({
  selector: 'app-not-found',
  templateUrl: './not-found.html',
  imports: [ErrorPage],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotFound {}
