import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ApiErrorService } from './api-error.service';
import { correlationInterceptor } from './correlation.interceptor';
import { problemDetailInterceptor } from './problem-detail.interceptor';

describe('HTTP foundation', () => {
  let client: HttpClient;
  let errors: ApiErrorService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([correlationInterceptor, problemDetailInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    client = TestBed.inject(HttpClient);
    errors = TestBed.inject(ApiErrorService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('adds a non-PII correlation identifier', () => {
    client.get('/api/v1/me').subscribe({ error: () => undefined });
    const request = http.expectOne('/api/v1/me');
    expect(request.request.headers.has('X-Correlation-ID')).toBe(true);
    request.flush({}, { status: 401, statusText: 'Unauthorized' });
  });

  it('records ProblemDetail and server correlation data without rendering internals', () => {
    client.get('/api/v1/me').subscribe({ error: () => undefined });
    const request = http.expectOne('/api/v1/me');
    request.flush(
      { status: 403, title: 'Forbidden', detail: 'Access is denied.' },
      { headers: { 'X-Correlation-ID': 'test-correlation-id' }, status: 403, statusText: 'Forbidden' },
    );

    expect(errors.latest()).toEqual({
      correlationId: 'test-correlation-id',
      problem: { status: 403, title: 'Forbidden', detail: 'Access is denied.' },
      status: 403,
    });
  });
});
