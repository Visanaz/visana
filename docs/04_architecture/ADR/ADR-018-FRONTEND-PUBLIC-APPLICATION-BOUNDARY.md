# ADR-018 — Límite sitio público / aplicación autenticada

**Estado:** PROPOSED.

**Contexto:** existen activos y documentos legales, pero no vistas/rutas originales completas. El catálogo Plan 3 actual es autenticado y `/viajero/search` no se mapea al dominio actual.

**Propuesta:** separar conceptualmente contenido público aprobado de la aplicación autenticada; mantener la opción de sitio estático separado y SPA para `/app`. La decisión depende de contenido, SEO, hosting, rutas y validación de producto.

**Consecuencia:** no se fuerza una única SPA ni se conserva búsqueda legado. FR-DG-02 decide límite y coexistencia.
