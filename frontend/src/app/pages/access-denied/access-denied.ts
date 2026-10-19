import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-access-denied',
  templateUrl: './access-denied.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccessDenied {}
