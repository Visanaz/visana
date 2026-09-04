# ADR-014 — Arquitectura Angular de frontend

**Estado:** PROPOSED.

**Contexto:** el legado es fuente parcial CodeIgniter/JS sin build recuperable; el backend Plan 3 expone contratos `/api/v1`. Se requiere una aplicación nueva mantenible sin portar JavaScript ni reglas financieras cliente.

**Propuesta:** Angular standalone, TypeScript y release estable soportada que se verificará al implementar; límites `core`, `shared` y `features` alineados a capacidades. Cada feature aísla rutas, componentes, facade/store, cliente y modelos. Componentes no realizan HTTP directo ni reglas financieras/autorización.

**Alternativas:** reconstruir PHP/CodeIgniter; SPA sin límites; microfrontends. Se descartan por falta de fuente completo, acoplamiento o complejidad no justificada.

**Consecuencia:** no congela major de Angular, librería UI ni estructura física final; requiere aprobación de alcance UX (FR-DG-01) antes de código.
