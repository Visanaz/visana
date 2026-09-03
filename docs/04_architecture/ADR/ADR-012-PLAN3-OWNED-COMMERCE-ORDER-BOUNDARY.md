# ADR-012 — Límite entre órdenes legacy y órdenes Plan 3

**Estado:** ACCEPTED_FOR_SPRINT_3

Las órdenes V1 tienen `affiliate_id` obligatorio y participan en una ruta heredada de confirmación simulada/evento de dominio. La cardinalidad entre `PlatformActor` y perfiles de negocio no está demostrada (DG-17). Por tanto, Sprint 3 crea `commerce_orders` con `owner_actor_id` y snapshots, sin modificar ni inferir la propiedad de órdenes V1.

Consecuencia: no hay migración automática, equivalencia de API ni semántica financiera implícita. La transición de datos requiere una decisión aprobada, una matriz de mapeo y reconciliación.
