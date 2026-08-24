---
trigger: always_on
---

# MCP REGLAS DE NEGOCIO - MULTINIVEL VISANA

## 1. Familias de Negocio Separadas
Existen dos modelos de compensación que operan en paralelo:
- **Modelo Afiliados:** Basado en niveles de profundidad (1 al 8), genealogía y comisiones por red.
- **Modelo Distribuidores:** Basado en volúmenes mensuales, pools globales de ventas y bonos de consistencia (permanencia).

## 2. Parámetros Confirmados (Fuente: BD y Documento)
- **Activación:** Compra mínima inicial de $200.000 COP.
- **Recompra:** Valor mensual mínimo exigido de $100.000 COP.
- **Porcentajes Unilevel (8 niveles):** L1: 15%, L2: 10%, L3: 5%, L4: 4%, L5: 3%, L6: 2%, L7: 1%, L8: 2%.
- **Pools de Distribuidores (Venta Global/Equipo):** >15M (1%), >25M (1.5%), >45M (2%), >100M (2.5%).
- **Bono Consistencia Distribuidor:** 6 meses sosteniendo ventas (0.5%), 12 meses (1%).
- **Liquidación:** Pagos los días 10 y 25 de cada mes. Retención en la fuente y Rete-ICA aplican.

## 3. 🔴 CONFLICTOS CRÍTICOS (PENDIENTES DE DEFINICIÓN POR EL NEGOCIO)
El Agente NO DEBE codificar la solución a estas reglas hasta que el PM o el Negocio lo autoricen:
- **AUD-001 (Duración de Activación):** El documento dice 30 días, las reglas anteriores 29 días, la BD actual dice 30 días. STATUS: 🔴 CONFLICTO.
- **AUD-002 (Bono Invitación Distribuidor):** El documento dice 10%. La BD actual dice 3% (de 0-5 referidos) y 5% (6+ referidos). STATUS: 🔴 CONFLICTO.
- **AUD-003 (Cálculo Team Sales):** En la BD legacy, la tabla `commission_plan_sale_types` indica que las compras, recompras y afiliaciones tienen `counts_for_team_sales = 0`. ¿Cómo se alimenta entonces el volumen de equipo para calificar niveles? STATUS: 🔴 CONFLICTO.