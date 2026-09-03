# Security Baseline

Baseline: OWASP Top 10, autenticación OIDC/JWT, RBAC + ownership, MFA administrativa, secretos gestionados externamente, validación, CORS/CSRF contextual, headers, rate limiting, SAST/dependency scanning, DAST posterior controlado y audit log.

Prioridades P0: confirmar payment provider con firma/idempotencia; evitar acceso cruzado de orden/pago/sponsor; políticas explícitas por recurso; auditoría de eventos financieros; secretos separados por ambiente y nunca en logs. No se ejecutó pentest ni se afirma vulnerabilidad explotable.
