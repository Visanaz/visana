import { InjectionToken } from '@angular/core';

export interface OidcAuthPort {
  checkAuthentication(): Promise<boolean>;
  login(): void;
  logout(): void;
}

export const OIDC_AUTH_PORT = new InjectionToken<OidcAuthPort>('OIDC_AUTH_PORT');
