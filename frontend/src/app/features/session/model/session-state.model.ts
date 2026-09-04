export type SessionPhase = 'configuration-required' | 'loading' | 'unauthenticated' | 'authenticated' | 'unlinked';

export interface SessionState {
  actorId: string | null;
  authorities: readonly string[];
  error: string | null;
  identityStatus: string | null;
  phase: SessionPhase;
}

export const initialSessionState: SessionState = {
  actorId: null,
  authorities: [],
  error: null,
  identityStatus: null,
  phase: 'loading',
};
