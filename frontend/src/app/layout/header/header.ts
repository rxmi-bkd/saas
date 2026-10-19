import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthenticationService } from '../../core/service/authentication.service';
import { ROUTE_URLS } from '../../core/router/route.constants';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header {
  private readonly authenticationService = inject(AuthenticationService);

  protected readonly isAuthenticated =
    this.authenticationService.isAuthenticated;

  protected readonly isAdmin = this.authenticationService.isAdmin;

  protected async logout(): Promise<void> {
    await this.authenticationService.logout();
  }

  protected readonly ROUTE_URLS = ROUTE_URLS;
}
