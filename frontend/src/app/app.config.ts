import { ApplicationConfig, Injector, provideAppInitializer, provideBrowserGlobalErrorListeners, inject } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideAuth, StsConfigLoader } from 'angular-auth-oidc-client';
import { createOidcConfigLoader } from './core/auth/oidc-config.loader';
import { RuntimeConfigService } from './core/config/runtime-config.service';
import { correlationInterceptor } from './core/http/correlation.interceptor';
import { problemDetailInterceptor } from './core/http/problem-detail.interceptor';
import { SessionFacade, sessionProviders } from './features/session/state/session.facade';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptors([correlationInterceptor, problemDetailInterceptor])),
    provideRouter(routes, withComponentInputBinding()),
    provideAuth({
      loader: {
        provide: StsConfigLoader,
        useFactory: createOidcConfigLoader,
        deps: [RuntimeConfigService],
      },
    }),
    ...sessionProviders,
    provideAppInitializer(() => {
      const runtimeConfig = inject(RuntimeConfigService);
      const injector = inject(Injector);
      return runtimeConfig.load().then(() => injector.get(SessionFacade).initialize());
    }),
  ],
};
