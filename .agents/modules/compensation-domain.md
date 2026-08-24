# Bounded Context: Compensation Engine

## Propósito
El motor de compensación es el corazón matemático del ERP. Toma una Orden de Compra pagada, la cruza con la Genealogía y las Calificaciones del Período, y genera `Commission` para cada ancestro que haya cumplido los candados del plan de compensación.

## Flujo de Dominio
Respeta el flujo inquebrantable de la arquitectura:

```
SALE -> ORDER -> PAYMENT CONFIRMED -> VOLUME ENGINE -> QUALIFICATION ->
GENEALOGY -> COMPENSATION ENGINE (este módulo) -> LEDGER -> SETTLEMENT -> PAYOUT
```

## Arquitectura del Dominio

### 1. Value Objects de Identidad
- **`CommissionId`**: UUID inmutable que identifica cada comisión de manera única.
- **`CommissionType`** (`DIRECT_BONUS`, `UNILEVEL_BONUS`, `POOL_BONUS`): Define la naturaleza de la comisión.
- **`CommissionStatus`** (`PENDING`, `CALCULATED`, `REVERSED`): Ciclo de vida de la comisión.

### 2. Entidad Inmutable: `Commission`
**Esta es la pieza más crítica del Ledger.** La entidad `Commission` es **inmutable**: una vez creada con `Commission.calculate(...)`, no expone setters ni modificadores. Esto implementa la regla de "Ledger Inmutable" (`architecture-rules.mcp.md`).

- Si el negocio necesita anular una comisión histórica, **no se borra ni se modifica**. En su lugar, se llama a `commission.reverse()`, que genera una nueva entidad `Commission` con estado `REVERSED` y un monto negativo. El saldo neto es cero, preservando la trazabilidad contable completa.
- El `CommissionId` de un reverso es **diferente** al original, garantizando que el registro histórico nunca se altera.

### 3. Domain Service: `UnilevelCompensationCalculatorService`
El servicio del plan Unilevel puro de DDD, sin `@Service` ni dependencias de Spring.

**Principio "Configuration over Hard-code":**

Los porcentajes `L1=15%, L2=10%...` **NO están escritos en el código fuente**. El método recibe un `Map<Integer, Percentage> unilevelPlan` inyectado externamente. Esto permite al negocio:
- Cambiar porcentajes desde un panel administrativo sin redeploy.
- Configurar un plan con solo 5 niveles (si el nivel 6 no tiene `Percentage`, el servicio lo omite).

**Algoritmo de Cálculo:**
1. Valida que la `Order` esté en estado `PAID`.
2. Calcula el total de la orden usando el VO `Money` (sin flotantes).
3. Itera el `upline` (ancestros ordenados del más cercano al más lejano) hasta un tope de 8 niveles.
4. Por cada nivel, verifica si hay `Percentage` configurado.
5. Verifica si el ancestro posee un `AffiliateQualification.isQualified() == true` para el periodo.
6. Si ambas condiciones se cumplen, genera una `Commission` con `amount = orderTotal * levelPercentage`.

## Casos de Prueba Críticos Cubiertos
| Caso | Resultado Esperado |
|---|---|
| 2 ancestros calificados, plan L1=15% L2=10%, orden $1000 | Comisión L1=$150, L2=$100 |
| L1 no calificado, L2 calificado | Solo se genera comisión para L2 |
| Ancestro sin registro de calificación | Se omite silenciosamente |
| Upline de 12 nodos, todos calificados | Máximo 8 comisiones |
| Plan parcial (solo define L1) | Solo comisión L1 |
| Orden en estado PENDING | Lanza `IllegalArgumentException` |
| `commission.reverse()` | Nueva entidad, monto negativo, ID distinto |

## Estado de Construcción
- 100% Java puro. Cero Spring, JPA, Lombok.
- Usa `Money.multiply(Percentage)` del Core Domain para operaciones financieras blindadas.
- `Commission.reverse()` implementa el Ledger Inmutable: sin deletes, sin updates históricos.
