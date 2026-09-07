# Registro de decisiones — Plan 3

**Corte vigente:** 2026-09-07

**Fuente funcional vigente:** SRC-010, formulario oficial de Daniel Reina

**Ratificación física:** `PENDING`

**Entrada en vigor:** `EFFECTIVE_FROM_PENDING`

La evidencia verbal y el documento manual de Gerencia se conservan como historia y soporte. Sus estados anteriores quedaron superados por SRC-010 cuando existe conflicto. La respuesta de prueba de Cristian está excluida. La aprobación digital no autoriza automáticamente código, efectos financieros, despliegue o retroactividad.

| ID | Estado vigente | Decisión canónica resumida | Aprobación | Pendiente explícito |
|---|---|---|---|---|
| DG-01 | `APPROVED_WITH_OBSERVATIONS` | Activación/recompra por aniversario mensual calendario | Digital, SRC-010 | Recompra temprana, expiración exacta y fin de mes |
| DG-02 | `APPROVED_DIGITAL` | Bono de invitación 10%; distribuidor activo | Digital, SRC-010 | Base y período |
| DG-03 | `APPROVED_DIGITAL` | Propias + toda downline; pagadas; cancelaciones/devoluciones restan | Digital, SRC-010 | Momento contable y parcialidades |
| DG-04 | `APPROVED_DIGITAL` | Aprobación automática por sistema | Digital, SRC-010 | Excepciones y reversión |
| DG-05 | `APPROVED_DIGITAL` | Created→Approved→Processing→Paid/Failed | Digital, SRC-010 | Retry, conciliación y comprobante |
| DG-06A | `PARTIAL` | Ventas globales de todos los distribuidores | Núcleo digital, SRC-010 | Base ambigua, tiers y reparto |
| DG-06B | `APPROVED_WITH_OBSERVATIONS` | Mantener/superar cada mes el mes anterior | Núcleo digital, SRC-010 | Caída, racha, 6/12 meses y base |
| DG-07 | `PARTIAL` | El 1% es adicional al Unilevel | Núcleo digital, SRC-010 | Base, elegibles, fórmula y período |
| DG-08 | `APPROVED_WITH_OBSERVATIONS` | Se detiene toda comisión de la rama | Núcleo digital, SRC-010 | Inicio, reversibilidad y volumen |
| DG-09 | `APPROVED_DIGITAL` | Qualification/Volume usa valor pagado por producto incluyendo IVA | Digital, SRC-010 | No extrapolar a otras fórmulas |
| DG-10 | `PARTIAL` | Cancelar antes de pago; descontar en período futuro después | Núcleo digital, SRC-010 | Parciales, rango y períodos cerrados |
| DG-11 | `OPEN_CRITICAL` | Período de calificación no definido | Sin respuesta completa | Ventana, límites, zona y ajustes |
| DG-12 | `BLOCKED` | Marco tributario sujeto a matriz oficial | Dirección digital, SRC-010 | Matriz contable |
| DG-13 | `APPROVED_WITH_OBSERVATIONS` | Total >=3 unidades, beneficio de envío a un destino | SRC-010 + SRC-011 | Geografía, valor, peso y excepciones |
| DG-14 | `OPEN_TECHNICAL_DECISION` | Recomendación actual: mantener Keycloak en esta etapa | Decisión delegada, SRC-010 | Adopción formal de recomendación |
| DG-15 | `APPROVED_WITH_OBSERVATIONS` | Dirección Banco de Occidente/Occired | Núcleo digital, SRC-010 | Discovery técnico completo |
| DG-16 | `APPROVED_WITH_OBSERVATIONS` | Auditar fuente futura y separar migración histórica | Núcleo digital, SRC-010 | Inventario, calidad y alcance |
| DG-17 | `APPROVED_DIGITAL` | Una identidad puede tener múltiples perfiles | Digital, SRC-010 | Matriz de permisos/transiciones |
| DG-18 | `PARTIAL` | Cambio admin-only, justificado, auditado y prospectivo | Núcleo digital, SRC-010 | `GENEALOGY_INACTIVITY_COMPRESSION_RULE` |
| DG-19 | `APPROVED_WITH_OBSERVATIONS` | Afiliación formal por aprobación administrativa | Núcleo digital, SRC-010 | Lifecycle separado |
| BR-L8 | `APPROVED_DIGITAL` | Comisión individual L8; no pool global | Digital, SRC-010 | Base, elegibilidad y reversos |
| QTH-F5-01 | `APPROVED_DIGITAL` | Umbrales L1–L8 confirmados | Digital, SRC-010 | `effectiveFrom` |
| BR-CANON | `APPROVED_DIGITAL` | 200k/100k, Unilevel y cortes confirmados | Digital, SRC-010 | Semánticas por beneficio |

## Conteo vigente

- `APPROVED_DIGITAL`: 9.
- `APPROVED_WITH_OBSERVATIONS/PARTIAL`: 11.
- `OPEN/BLOCKED`: 3.
- Núcleos con decisión digital y ratificación física pendiente: 20.

## Decisiones frontend relacionadas no modificadas

| ID | Estado | Pendiente |
|---|---|---|
| FR-DG-01 | `SIGUE_ABIERTO` | Alcance UX TO-BE |
| FR-DG-02 | `SIGUE_ABIERTO` | Sitio público vs aplicación autenticada |
| FR-DG-03 | `SIGUE_ABIERTO` | Design system / librería UI |
| FR-DG-04 | `SIGUE_ABIERTO` | Hosting objetivo frontend |
| FR-DG-05 | `SIGUE_ABIERTO` | Paridad visual legado |

## Gobernanza

- SRC-010 reemplaza decisiones provisionales incompatibles: `SUPERSEDES_PROVISIONAL_VERSION`.
- La versión provisional Catherine 2026-09-05 y sus resultados históricos permanecen intactos.
- Conforme a ADR-027, una futura versión aprobada se crea como nueva versión y no reinterpreta resultados previos.
- No puede persistirse una versión efectiva sin fecha aprobada; mientras falte, se usa `EFFECTIVE_FROM_PENDING` documentalmente.
- F5 permanece `NOT_STARTED / NOT_AUTHORIZED` y `BLOCKED_BY_DG11`.
