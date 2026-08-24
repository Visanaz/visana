---
trigger: always_on
---

# MCP ARQUITECTURA - REGLAS INQUEBRANTABLES VISANA

1. **Patrón Base:** Clean Architecture + DDD. Las dependencias siempre apuntan hacia adentro.
2. **Fuente de Verdad Arquitectónica (Regla de Oro):** La base de datos actual (MySQL legacy) es una fuente de evidencia histórica y operativa, pero **NO ES LA FUENTE DE VERDAD ARQUITECTÓNICA**. La fuente de verdad del nuevo VISANA es el Domain Model construido a partir de las Business Rules aprobadas. NO copiar la BD legacy a Spring Boot.
3. **Bounded Contexts Definidos:** El sistema se separa en: Identity, Customer, Affiliate, Distributor, Network, Genealogy, Commerce, Orders, Payments, Qualification, Volume, Compensation, Ledger, Settlement, Reporting.
4. **Genealogy Domain:** Nunca implementar la genealogía (red) como consultas SQL improvisadas. Debe ser un dominio explícito con `Sponsor`, `Ancestor`, `Descendant`, `Upline`, `Downline`.
5. **Ledger Inmutable:** Toda comisión monetaria debe tener trazabilidad (Commission -> LedgerTransaction -> Payout). Nunca modificar silenciosamente una comisión histórica; usar `Adjustment` o `Reversal`.