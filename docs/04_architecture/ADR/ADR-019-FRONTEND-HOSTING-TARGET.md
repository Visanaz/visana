# ADR-019 — Hosting de frontend

**Estado:** PROPOSED.

**Contexto:** la arquitectura backend propone Google Cloud, pero no hay decisión de hosting frontend, presupuesto, dominios, CDN, headers ni operación. El legado cPanel no es un destino objetivo probado.

**Propuesta:** evaluar Firebase Hosting, Cloud Storage + CDN, Cloud Run static delivery u otra alternativa contra costo, soporte de SPA routing, CSP/headers, rollback, integración con dominios y operación. No se selecciona proveedor.

**Consecuencia:** FR-DG-04 permanece abierto; no se crean infraestructura, DNS, CI/CD ni configuración CORS.
