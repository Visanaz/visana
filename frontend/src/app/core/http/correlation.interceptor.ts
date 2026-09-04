import { HttpInterceptorFn } from '@angular/common/http';

export const correlationInterceptor: HttpInterceptorFn = (request, next) => {
  if (request.headers.has('X-Correlation-ID')) {
    return next(request);
  }

  return next(request.clone({ setHeaders: { 'X-Correlation-ID': crypto.randomUUID() } }));
};
