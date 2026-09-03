# Modelo de autorización objetivo

Autenticación y autorización son responsabilidades distintas. La identidad emite claims; una policy layer decide acceso por rol **y** ownership del recurso.

```text
request -> token validation -> actor context -> role policy + resource ownership -> application service
```

Para orden, confirmación de pago y patrocinador, la policy debe evaluar actor, acción, recurso, relación organizacional/tenant si aplica y delegación explícita. No se asume que `tenant_id` demuestre multitenancy ni que un ID pueda cruzar recursos.

Audit log futuro: WHO, WHAT, RESOURCE, BEFORE/AFTER cuando sea permitido, WHEN, CORRELATION y SOURCE. Eventos de compensación, calificación, ledger, settlement, payout, roles y pago son prioritarios. MFA para administración, scopes mínimos, validación de entrada, CORS/CSRF según flujo, rate limiting, headers, secret management y escaneo de dependencias quedan como baseline de seguridad.
