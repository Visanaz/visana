export interface ProblemDetail {
  detail?: string;
  status?: number;
  title?: string;
  type?: string;
}

export function isProblemDetail(value: unknown): value is ProblemDetail {
  return typeof value === 'object' && value !== null;
}
