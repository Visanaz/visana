import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { ProblemDetail, isProblemDetail } from './problem-detail.model';

export interface ApiErrorState {
  correlationId: string | null;
  problem: ProblemDetail | null;
  status: number | null;
}

@Injectable({ providedIn: 'root' })
export class ApiErrorService {
  readonly latest = signal<ApiErrorState | null>(null);

  record(error: HttpErrorResponse): void {
    this.latest.set({
      correlationId: error.headers.get('X-Correlation-ID'),
      problem: isProblemDetail(error.error) ? error.error : null,
      status: error.status || null,
    });
  }
}
