import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { OIDC_AUTH_PORT } from '../../../core/auth/oidc-auth.port';
import { RuntimeConfigService } from '../../../core/config/runtime-config.service';
import { ME_API_PORT } from '../data-access/me-api.port';
import { SessionFacade } from './session.facade';

describe('SessionFacade', () => {
  const runtimeConfig = { isOidcConfigured: signal(true) } as Pick<RuntimeConfigService, 'isOidcConfigured'>;

  it('represents an unlinked actor without deriving a business profile', async () => {
    await TestBed.configureTestingModule({
      providers: [
        SessionFacade,
        { provide: RuntimeConfigService, useValue: runtimeConfig },
        { provide: OIDC_AUTH_PORT, useValue: { checkAuthentication: () => Promise.resolve(true), login: () => undefined, logout: () => undefined } },
        { provide: ME_API_PORT, useValue: { current: () => Promise.resolve({ actorId: null, authorities: [], identityStatus: 'UNLINKED_IDENTITY' }) } },
      ],
    }).compileComponents();

    const facade = TestBed.inject(SessionFacade);
    await facade.initialize();

    expect(facade.state()).toMatchObject({ phase: 'unlinked', actorId: null, identityStatus: 'UNLINKED_IDENTITY' });
  });

  it('requires OIDC runtime configuration before checking a session', async () => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [
        SessionFacade,
        { provide: RuntimeConfigService, useValue: { isOidcConfigured: signal(false) } },
        { provide: OIDC_AUTH_PORT, useValue: { checkAuthentication: () => Promise.resolve(true), login: () => undefined, logout: () => undefined } },
        { provide: ME_API_PORT, useValue: { current: () => Promise.resolve({ actorId: null, authorities: [], identityStatus: 'LINKED' }) } },
      ],
    });

    const facade = TestBed.inject(SessionFacade);
    await facade.initialize();

    expect(facade.state().phase).toBe('configuration-required');
  });
});
