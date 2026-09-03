# Product Gap Map - Plan 3

```text
PLAN 3
├── Identity          PARTIAL              GAP-014
├── Commerce          PARTIAL              GAP-011, GAP-026
├── Network           BLOCKED_BY_SOURCE    GAP-004, GAP-008, GAP-021
├── Compensation      BLOCKED_BY_DECISION  GAP-001..010
├── Finance           MISSING              GAP-012, GAP-013, GAP-028
├── Reporting         MISSING              GAP-017, GAP-027
├── Security          PARTIAL              GAP-015, GAP-016
├── Data              PARTIAL              GAP-018..021
├── QA                PARTIAL              GAP-022..024
└── Infrastructure    PARTIAL              GAP-021, GAP-022
```

| Rama | Estado | Hecho AS-IS | Bloqueador principal | Recursos API a validar, sin diseñar UI |
|---|---|---|---|---|
| Identity | PARTIAL | JWT/Keycloak converter y `TenantId` existen | Mapeo actor-rol-usuario legado | Sesion/identidad y perfil de actor. |
| Commerce | PARTIAL | Endpoints de orden y JPA V1 existen | Pago real, catalogo/POS y ownership | Ordenes, items, productos y confirmaciones. |
| Network | BLOCKED_BY_SOURCE | Modelo de nodo existe; puerto/adaptadores fuente faltan | GAP-021 y definicion de red | Afiliados, patrocinadores, arbol, upline/downline. |
| Compensation | BLOCKED_BY_DECISION | Unilevel parametrico existe | DG-01,02,03,06,07,08,09,11 | Plan, calificación, volumen, comisiones e incentivos. |
| Finance | MISSING | Commission y Payout aislados | Cadena ledger/approval/payout/tax | Ledger, liquidaciones, ordenes y pagos. |
| Reporting | MISSING | No modulo fuente Java | Alcance y datos financieros cerrados | Reportes de comisiones, volumen, pagos y auditoria. |
| Security | PARTIAL | JWT, roles extraidos y autenticacion general | Ownership/roles por recurso | Autorizacion de orden, pago y patrocinador. |
| Data | PARTIAL | SQL legado y V1 coexisten | Mapeo y sentinelas | Estado de migracion/conciliacion, no CRUD inferido. |
| QA | PARTIAL | Unidades fuente/historico Surefire | P0 sin integracion/E2E/runtime | Evidencia de pruebas, contratos y seguridad. |
| Infrastructure | PARTIAL | Maven, Flyway, MySQL y Keycloak declarados | Fuente/binarios no reproducibles; no se ejecuto | Health/operacion solo tras validar despliegue. |

`READY` no se asigna a ninguna rama: no hay evidencia runtime integral ni trazabilidad completa de un flujo financiero del Plan 3. `BLOCKED_BY_SOURCE` no afirma inexistencia fuera del clon; identifica que el clon no contiene fuente suficiente.
