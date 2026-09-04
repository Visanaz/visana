import { z } from 'zod';

const oidcConfigSchema = z.object({
  authority: z.string().url(),
  clientId: z.string().min(1),
  scope: z.string().min(1),
  redirectUrl: z.string().url(),
  postLogoutRedirectUrl: z.string().url().optional(),
});

export const runtimeConfigSchema = z.object({
  apiBaseUrl: z.string(),
  oidc: oidcConfigSchema.nullable(),
});

export type RuntimeConfig = z.infer<typeof runtimeConfigSchema>;

export type RuntimeConfigStatus = 'loading' | 'ready' | 'invalid';
