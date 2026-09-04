# Continuous Engineering Loop

Antes de cada ciclo de implementación, Codex debe leer la gobernanza aplicable, `PROJECT_STATE.md`, decisiones, gaps y ADRs del módulo. Todo ciclo sigue: analizar, implementar, probar, revisar seguridad, actualizar documentación/estado/trazabilidad, commit, push, PR, reportar el siguiente paso seguro y detenerse para revisión PM.

## Definition of Done

Código, tests, security review, documentación, trazabilidad, estado de proyecto y reporte de sprint deben concordar. Un cambio no está terminado sólo porque compile. No se cierran gaps o decision gates sin la evidencia/decisión exigida.

## Git y handoff

El trabajo nace de `dev`, llega por PR y no se desarrolla directamente en `dev`, `qa` o `main`. Cada entrega registra base, validaciones, cambios, límites, gates pendientes y siguiente revisión PM.
