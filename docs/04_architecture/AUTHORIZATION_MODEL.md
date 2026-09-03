# Modelo de autorización Plan 3

Autenticación y autorización son responsabilidades distintas. La identidad emite claims; una policy layer decide acceso por rol **y** ownership del recurso.

```text
request -> token validation -> OIDC issuer + subject -> PlatformActor -> role policy + resource ownership -> application service
```

Para orden, confirmación de pago y patrocinador, la policy evalúa actor interno, acción y ownership explícito. Si ownership no existe o no demuestra coincidencia, responde DENY. No se asume que `tenant_id`, `affiliate_id` o un ID legado demuestre ownership. Para evitar enumeración, la verificación de ownership precede al use case de confirmación; recursos inexistentes, ajenos o sin ownership no pasan a la capa financiera.

Los roles extraídos por Keycloak se mantienen como authorities de autenticación, pero no existe una matriz fuente para grants administrativos o bypass de ownership. Todo override privilegiado es `BLOCKED_BY_ROLE_MATRIX`. `SPONSOR_RELATIONSHIP` dispone de un seam fail-closed, sin endpoint ni regla de genealogía nueva.

Audit log futuro: WHO, WHAT, RESOURCE, BEFORE/AFTER cuando sea permitido, WHEN, CORRELATION y SOURCE. Eventos de compensación, calificación, ledger, settlement, payout, roles y pago son prioritarios. MFA para administración, scopes mínimos, validación de entrada, CORS/CSRF según flujo, rate limiting, headers, secret management y escaneo de dependencias quedan como baseline de seguridad.
