# Bounded Context: Ledger & Settlement

## Propósito
Este módulo maneja las finanzas internas de los afiliados (Ledger) y el proceso de retirar ese dinero hacia el mundo exterior (Settlement). Cumple la función de "Billetera Virtual", garantizando que el dinero nunca desaparezca sin dejar rastro y que los afiliados nunca puedan retirar más de lo que poseen.

## Flujo de Dominio

```
COMPENSATION ENGINE -> LEDGER (Crédito)
SETTLEMENT (Débito) -> PAYOUT -> Pago Bancario
```

## Arquitectura del Dominio

### 1. Value Objects
- **`LedgerAccountId`**, **`TransactionId`**, **`PayoutId`**: UUIDs fuertemente tipados.
- **`TransactionType`**: Enum `CREDIT` / `DEBIT`.
- **`PayoutStatus`**: Estados para orquestar pagos: `PENDING`, `PROCESSING`, `PAID`.

### 2. Entidades Principales

#### `LedgerAccount` (Aggregate Root)
- Administra el `currentBalance` (usando el seguro VO `Money`).
- **Fail-Fast**: El método `registerDebit` lanza `InsufficientFundsException` si se intenta debitar más del saldo disponible. 
- Bloquea internamente el intento de aplicar VOs `Money` reversados (montos negativos) para mantener la claridad contable: un descuento es un débito de un monto positivo.

#### `LedgerTransaction` (Historial Inmutable)
- Entidad puramente descriptiva, inmutable tras la creación. 
- Funciona como un apunte contable que justifica cada movimiento del `LedgerAccount`. En el futuro, la suma de estas transacciones servirá para hacer auditoría de saldos y asegurar que cuadren con el `currentBalance`.

#### `Payout` (Aggregate Root de Settlement)
- Representa la "Orden de Pago".
- Nace en estado `PENDING`. Tiene transiciones de estado a `PROCESSING` y `PAID`.

### 3. Domain Service: `SettlementService`
- **Operación Crítica:** `generatePayout(account, period)`
- Centraliza la lógica de liquidación:
  1. Verifica que haya fondos.
  2. Debita TODO el saldo de la cuenta `LedgerAccount`, dejándola en 0.
  3. Crea y retorna la entidad `Payout` por el monto total.
- Al encapsular esta lógica en un Domain Service puro, garantizamos que no haya fuga de reglas contables hacia la capa de aplicación.

## Estado de Construcción
- Capas de `com.visana.erp.ledger.domain.model` y `.../service` implementadas.
- 100% Java puro, sin frameworks persistentes.
- Suite de pruebas JUnit 5 en `SettlementServiceTest` comprobando que las reglas contables no se rompen (0% tolerancia a saldos negativos) y que la liquidación extrae los fondos correctamente.
