# ADR-015 — State management de frontend

**Estado:** PROPOSED.

**Contexto:** catálogo, órdenes, red e identidad tienen estados de servidor separados; no hay evidencia de necesidad de un event store global.

**Propuesta:** Angular Signals con facades/stores scoped por feature. Estado local queda en componentes, server state en la feature y sólo sesión/contexto técnico se comparte desde core. NgRx no se adopta por defecto.

**Alternativas:** servicios globales mutables, NgRx desde el inicio, o estado exclusivamente en componentes. La primera y tercera aumentan acoplamiento; NgRx exige justificación por coordinación/eventos reales.

**Consecuencia:** ni components ni stores son fuente de precio, impuestos, comisiones, eligibility o authorization; una reevaluación futura debe justificarse con flujos cross-feature medidos.
