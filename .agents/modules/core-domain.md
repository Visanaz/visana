# Bounded Context: Core Domain

## Propósito
Este módulo establece las fundaciones técnicas y de negocio para toda la plataforma Visana. Representa la capa más interna de la Clean Architecture, libre de dependencias externas (Spring, JPA, etc.) garantizando que las reglas de negocio sean puras, inmutables y predecibles.

## Componentes Implementados (Value Objects)

Los Value Objects han sido implementados utilizando `record` en Java 21, garantizando inmutabilidad, fail-fast validations (validación en el constructor) y pureza del modelo de dominio.

### 1. `Money`
- **Descripción**: Encapsula un valor monetario (`BigDecimal`).
- **Protección de Reglas de Negocio**:
  - **Precisión**: Usa `BigDecimal` internamente y aplica un redondeo consistente (`HALF_UP`) para evitar problemas de precisión inherentes al uso de tipos de coma flotante (`double` o `float`).
  - **Invariantes de Valores Positivos**: Por regla de negocio, el dinero estándar nunca puede ser negativo. Se rechazan valores menores a cero en el momento de la construcción.
  - **Manejo de Reversos**: Soporta de manera explícita la creación de valores negativos a través de un factory method `Money.reversal(amount)`, protegiendo el historial financiero (Ledger) contra mutaciones silenciosas y dando semántica real al modelo contable.
  - **Operaciones Aritméticas Seguras**: Toda operación (`add`, `subtract`, `multiply`) genera una nueva instancia inmutable de `Money`.

### 2. `Percentage`
- **Descripción**: Representa un valor porcentual usado para el cálculo de comisiones y bonos (por ejemplo, 15% del Unilevel).
- **Protección de Reglas de Negocio**:
  - Evita el uso de tipos crudos (ej. `0.15` como `double`).
  - **Fail-fast**: Verifica en el constructor que los porcentajes no sean negativos, previniendo alteraciones perjudiciales en los cálculos del motor de compensación (Compensation Engine).

### 3. `Period`
- **Descripción**: Representa un periodo de calificación mensual en el cual aplican los volúmenes, comisiones y reglas de negocio.
- **Protección de Reglas de Negocio**:
  - Encapsula la complejidad de formato y temporalidad mediante `YearMonth` de Java.
  - Define estrictamente el contexto temporal de una comisión (ej. "2026-06").
  - Facilita cálculos secuenciales de manera semántica con métodos como `next()` y `previous()`, lo cual es vital para los Bonos de Consistencia de 6 y 12 meses.

### 4. `TenantId`
- **Descripción**: El identificador unívoco de la empresa u organización en la plataforma Multitenant.
- **Protección de Reglas de Negocio**:
  - Encapsula un `UUID` y blinda el "Multitenancy" en toda la aplicación.
  - Previene "Insecure Direct Object Reference" (IDOR) y fugas de datos entre distintos esquemas de red. Ninguna operación de base de datos o lógica de negocio procederá si no opera de manera explícita bajo un `TenantId` válido.

## Estado de Construcción
- Capas creadas siguiendo Clean Architecture (`com.visana.erp.core.domain.model`).
- Cero dependencias hacia librerías de infraestructura o persistencia en los Value Objects.
- Suite de Pruebas con cobertura >90% en lógica de creación y limit cases (JUnit 5).

---
**Nota de Arquitectura**: La pureza de este modelo permitirá más adelante alimentar un "Ledger inmutable" sin temor a que errores de redondeo o datos no validados en capas superficiales corrompan los pagos a Afiliados y Distribuidores.
