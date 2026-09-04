import { Injectable, inject, signal } from '@angular/core';
import { AngularOidcAuthAdapter } from '../../../core/auth/angular-oidc-auth.adapter';
import { OIDC_AUTH_PORT } from '../../../core/auth/oidc-auth.port';
import { RuntimeConfigService } from '../../../core/config/runtime-config.service';
import { CodegenBlockedMeApiAdapter, FrontendApiCodegenBlockedError, ME_API_PORT } from '../data-access/me-api.port';
import { SessionState, initialSessionState } from '../model/session-state.model';

@Injectable({ providedIn: 'root' })
export class SessionFacade {
  private readonly oidc = inject(OIDC_AUTH_PORT);
  private readonly runtimeConfig = inject(RuntimeConfigService);
  private readonly meApi = inject(ME_API_PORT);

  readonly state = signal<SessionState>(initialSessionState);

  async initialize(): Promise<void> {
    if (!this.runtimeConfig.isOidcConfigured()) {
      this.state.set({ ...initialSessionState, phase: 'configuration-required' });
      return;
    }

    this.state.set({ ...initialSessionState, phase: 'loading' });
    const authenticated = await this.oidc.checkAuthentication();

    if (!authenticated) {
      this.state.set({ ...initialSessionState, phase: 'unauthenticated' });
      return;
    }

    try {
      const actor = await this.meApi.current();
      this.state.set({
        actorId: actor.actorId,
        authorities: actor.authorities,
        error: null,
        identityStatus: actor.identityStatus,
        phase: actor.identityStatus === 'UNLINKED_IDENTITY' ? 'unlinked' : 'authenticated',
      });
    } catch (error: unknown) {
      const message = error instanceof FrontendApiCodegenBlockedError ? error.message : 'SESSION_CONTEXT_UNAVAILABLE';
      this.state.set({ ...initialSessionState, error: message, phase: 'unauthenticated' });
    }
  }

  beginLogin(): void {
    this.oidc.login();
  }

  logout(): void {
    this.oidc.logout();
  }
}

export const sessionProviders = [
  { provide: OIDC_AUTH_PORT, useClass: AngularOidcAuthAdapter },
  { provide: ME_API_PORT, useClass: CodegenBlockedMeApiAdapter },
];
