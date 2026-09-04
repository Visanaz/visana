import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ApiErrorService } from './api-error.service';

export const problemDetailInterceptor: HttpInterceptorFn = (request, next) => {
  const errors = inject(ApiErrorService);

  return next(request).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse) {
        errors.record(error);
      }

      return throwError(() => error);
    }),
  );
};
