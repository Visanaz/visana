# Bounded Context: Commerce & Orders

## Propósito
Este módulo es el motor financiero de entrada del sistema. Modela los productos y las órdenes de compra (ingresos de dinero). Es fundamental ya que el dinero pagado en estas órdenes será luego procesado por el "Volume Engine" y repartido en el "Compensation Engine".

## Arquitectura del Dominio

### 1. Value Objects de Identidad e Inmutabilidad
- **`ProductId` y `OrderId`**: `record` inmutables encapsulando `UUID`s. Aseguran tipado estricto contra inyecciones e intercambios accidentales de ID en las capas superiores.
- **`Money` (Reutilizado del Core)**: Cada transacción monetaria (como el precio de un producto, subtotal de un ítem y total de una orden) está estrictamente gobernado por `Money`, previniendo errores de redondeo de punto flotante.

### 2. Entidades Secundarias
- **`Product`**: Contiene la definición base del producto. Destaca el flag `isCommissionable`, vital para decidir si su compra generará o no volumen comisionable en la red.
- **`OrderItem`**: Una entidad puramente local de la Orden (Local Entity), que define un detalle de compra (producto, cantidad, precio). Calcula su propio subtotal internamente usando el VO `Money`.

### 3. Aggregate Root (`Order`)
Representa una Orden de Compra en el sistema.
- **Invariantes y Encapsulamiento**:
  - `List<OrderItem> items` está encapsulada (se retorna como `unmodifiableList`). No se pueden añadir ítems arbitrariamente si la orden no está en estado `PENDING`.
  - El método `calculateTotal()` recorre iterativamente los ítems y acumula el total operando matemáticamente con instancias inmutables de `Money`.
  - **Máquina de Estados de Pago**: El método `confirmPayment()` transita el estado a `PAID`. La entidad lanza excepciones (`IllegalStateException`) si se intenta pagar una orden vacía, ya cancelada, o ya pagada. Protegiendo al sistema de dobles cobros o conciliaciones fantasma.
  - Al confirmarse el pago, el Dominio está listo conceptualmente para despachar un Dominio Evento (ej. `OrderPaidEvent`), que será el disparador principal para el motor de calificación y comisiones en la red (arquitectura reactiva).

## Estado de Construcción
- Cero acoplamiento a Base de Datos (JPA) o inyección de dependencias (Spring).
- Operaciones monetarias blindadas.
- Pruebas unitarias de las transiciones de pago y acumulación de saldos.
