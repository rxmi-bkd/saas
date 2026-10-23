import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-status-message',
  templateUrl: './status-message.html',
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StatusMessage {
  readonly heading = input.required<string>();
  readonly description = input.required<string>();
  readonly linkLabel = input.required<string>();
  readonly linkUrl = input.required<string>();
}
