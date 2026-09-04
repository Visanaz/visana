import { StsConfigLoader, StsConfigStaticLoader } from 'angular-auth-oidc-client';
import { RuntimeConfigService } from '../config/runtime-config.service';

export function createOidcConfigLoader(runtimeConfig: RuntimeConfigService): StsConfigLoader {
  const oidc = runtimeConfig.config()?.oidc;

  if (oidc === null || oidc === undefined) {
    return new StsConfigStaticLoader([]);
  }

  return new StsConfigStaticLoader({
    authority: oidc.authority,
    clientId: oidc.clientId,
    postLogoutRedirectUri: oidc.postLogoutRedirectUrl,
    redirectUrl: oidc.redirectUrl,
    responseType: 'code',
    scope: oidc.scope,
  });
}
