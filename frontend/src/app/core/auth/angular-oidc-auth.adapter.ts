import { Injectable, inject } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { firstValueFrom } from 'rxjs';
import { OidcAuthPort } from './oidc-auth.port';

@Injectable()
export class AngularOidcAuthAdapter implements OidcAuthPort {
  private readonly oidc = inject(OidcSecurityService);

  async checkAuthentication(): Promise<boolean> {
    const response = await firstValueFrom(this.oidc.checkAuth());
    return response.isAuthenticated;
  }

  login(): void {
    this.oidc.authorize();
  }

  logout(): void {
    this.oidc.logoff();
  }
}
