import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { RuntimeConfigService } from './runtime-config.service';

describe('RuntimeConfigService', () => {
  let service: RuntimeConfigService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(RuntimeConfigService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('accepts a configured runtime document without secrets', async () => {
    const loading = service.load();
    const request = http.expectOne('assets/runtime-config.json');
    request.flush({
      apiBaseUrl: 'http://localhost:8080',
      oidc: {
        authority: 'http://localhost:8180/realms/visana',
        clientId: 'visana-spa',
        scope: 'openid profile',
        redirectUrl: 'http://localhost:4200/auth/callback',
      },
    });
    await loading;

    expect(service.status()).toBe('ready');
    expect(service.isOidcConfigured()).toBe(true);
  });

  it('marks malformed configuration as invalid', async () => {
    const loading = service.load();
    http.expectOne('assets/runtime-config.json').flush({ apiBaseUrl: 1, oidc: null });
    await loading;

    expect(service.status()).toBe('invalid');
  });
});
