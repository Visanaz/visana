import { inject } from '@angular/core';
import { CanMatchFn, Router } from '@angular/router';
import { SessionFacade } from './state/session.facade';

export const linkedIdentityGuard: CanMatchFn = () => {
  const router = inject(Router);
  const session = inject(SessionFacade).state();

  if (session.phase === 'authenticated') {
    return true;
  }

  return router.parseUrl(session.phase === 'unlinked' ? '/unlinked' : '/unauthorized');
};
