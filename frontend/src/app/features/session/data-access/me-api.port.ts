import { InjectionToken } from '@angular/core';

/**
 * Boundary for the future generated `/api/v1/me` client.
 * This is intentionally not a handwritten wire DTO.
 */
export interface CurrentActorContext {
  actorId: string | null;
  authorities: readonly string[];
  identityStatus: string;
}

export interface MeApiPort {
  current(): Promise<CurrentActorContext>;
}

export const ME_API_PORT = new InjectionToken<MeApiPort>('ME_API_PORT');

export class FrontendApiCodegenBlockedError extends Error {
  constructor() {
    super('FRONTEND_API_CODEGEN_BLOCKED_BY_SPEC');
  }
}

export class CodegenBlockedMeApiAdapter implements MeApiPort {
  current(): Promise<CurrentActorContext> {
    return Promise.reject(new FrontendApiCodegenBlockedError());
  }
}
