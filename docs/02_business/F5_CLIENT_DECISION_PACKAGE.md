# Paquete oficial de decisiones VISANA previo a F5

**Fecha de preparación:** 2026-09-07
**Estado:** `PARA_RESPUESTA_Y_APROBACIÓN_DE_VISANA`
**Reglas aprobadas al emitir el paquete:** 0

## Instrucciones para VISANA

Este documento reúne únicamente las decisiones que determinan la futura pantalla F5 de activación, volumen y calificación. Marque una opción por pregunta, complete “Otra” cuando corresponda y diligencie los campos de aprobación. Ninguna opción está preseleccionada y ningún ejemplo constituye una regla aprobada.

Las fuentes se interpretan en este orden: (1) documento oficial firmado por VISANA, (2) formulario oficial diligenciado por VISANA, (3) aclaración escrita explícita de VISANA, (4) aclaración verbal documentada como provisional, (5) código o datos históricos y (6) inferencia técnica. Una fuente inferior no reemplaza una superior. Las fuentes 4 a 6 no pueden producir estado `APPROVED`.

Cada aprobación futura debe identificar código, versión, fecha efectiva, evidencia, aprobador y fecha de aprobación. Las reglas nuevas se aplicarán mediante versiones; no reescribirán silenciosamente resultados históricos.

## DG-01 — Activación mensual y fecha de vencimiento

**Estado actual:** `PARTIAL_CLIENT_DECISION_REQUIRED` / `NO_APROBADA`.

**Fuente actual:** aclaración verbal atribuida a Catherine del 2026-09-05, conservada en la Working Baseline provisional y el Decision Register.

### Entendimiento actual

Una aclaración verbal provisional indica que la activación comienza después de un pago confirmado, en la fecha y hora de esa confirmación, y dura un mes calendario. Este entendimiento es `PROVISIONAL — NOT APPROVED`.

### Punto no resuelto

VISANA debe confirmar el entendimiento provisional y definir el momento exacto de vencimiento y el tratamiento de una recompra realizada mientras todavía existe activación vigente.

### Ejemplo concreto

Un miembro confirma su pago el 05/09/2026 a las 14:35. La fecha aniversario es 05/10/2026. Luego confirma una recompra el 25/09/2026. Se necesita una única regla para determinar hasta cuándo está activo.

### Decisiones seleccionables

**1. Confirmación del entendimiento provisional**

- [ ] Sí: inicia con pago confirmado, en `paymentConfirmedAt`, y dura un mes calendario.
- [ ] No. Regla correcta: ____________________________________________
- [ ] Otra aclaración: _______________________________________________

**2. Momento de vencimiento**

- [ ] A. En el instante exacto del aniversario mensual; en el ejemplo, 05/10/2026 a las 14:35.
- [ ] B. Es válido hasta el final de la fecha aniversario; en el ejemplo, hasta finalizar el 05/10/2026.
- [ ] C. Otra: _______________________________________________________

**3. Recompra mientras la activación sigue vigente**

- [ ] A. Reinicia un mes desde la fecha y hora del pago de la recompra.
- [ ] B. Extiende la fecha de expiración vigente por un mes.
- [ ] C. La recompra sólo aplica al siguiente período definido por VISANA.
- [ ] D. Otra: _______________________________________________________

### Por qué importa

Determina el estado activo, la fecha visible de vencimiento y la elegibilidad que dependa de la activación.

### Interpretación técnica segura recomendada

No mostrar estado, fecha ni cuenta regresiva como información canónica antes de la aprobación. La regla aprobada debe tener zona horaria, versión y fecha efectiva explícitas, sin recalcular silenciosamente el pasado.

**Código:** DG-01
**Respuesta final de VISANA:** _________________________________________________
**Otra aclaración:** ___________________________________________________________
**Aprobador y cargo:** _________________________________________________________
**Fecha de aprobación:** ____________________
**Versión:** ____________________  **effectiveFrom:** ____________________
**Evidencia/fuente oficial:** __________________________________________________

## DG-03 — ¿Qué venta cuenta para Team Sales?

**Estado actual:** `BLOCKED` / `NO_APROBADA`.

**Fuente actual:** aclaración verbal del 2026-09-05 y evidencia histórica contrastadas en la Working Baseline provisional y el Decision Register.

### Entendimiento actual

Una aclaración verbal provisional propone sumar ventas calificables propias y de todos los descendientes. No existe definición aprobada de “venta calificable”.

### Punto no resuelto

Cada elemento siguiente debe decidirse por separado para evitar que alcance de red, estado de pago, cancelaciones, reembolsos y período queden mezclados en una respuesta ambigua.

### Ejemplo concreto

Laura registra una venta propia de COP 100.000. Sus descendientes Andrés y Sofía registran COP 200.000 y COP 300.000. La venta de Sofía se cancela antes de la calificación y Andrés recibe después un reembolso parcial de COP 50.000. VISANA debe indicar cuánto cuenta y cuándo.

### Decisiones seleccionables

**1. ¿La venta propia del miembro se incluye?**

- [ ] Sí.
- [ ] No.
- [ ] Otra condición: _______________________________________________

**2. ¿Qué descendientes se incluyen?**

- [ ] Ninguno.
- [ ] Sólo descendientes directos.
- [ ] Todos los descendientes.
- [ ] Otro conjunto o profundidad: __________________________________

**3. ¿Sólo cuenta una venta con pago confirmado?**

- [ ] Sí, únicamente después de la confirmación del pago.
- [ ] No. Estado o combinación requerida: ____________________________
- [ ] Otra: _________________________________________________________

**4. Cancelación antes de evaluar la calificación**

- [ ] La venta cancelada se excluye completamente.
- [ ] La venta conserva valor bajo estas condiciones: _________________
- [ ] Otra: _________________________________________________________

**5. Reembolso total después de una calificación ya registrada**

- [ ] Registrar el ajuste contra el período original.
- [ ] Registrar el ajuste en el período del reembolso.
- [ ] No ajustar la calificación histórica; tratamiento alternativo: ___
- [ ] Otra: _________________________________________________________

**6. Reembolso parcial**

- [ ] Restar únicamente el monto reembolsado.
- [ ] Excluir toda la venta.
- [ ] Aplicar otra regla: ____________________________________________

**7. Fecha que determina el período de inclusión**

- [ ] Fecha/hora de confirmación del pago.
- [ ] Fecha/hora de creación de la orden.
- [ ] Fecha/hora de entrega/finalización.
- [ ] Otra: _________________________________________________________

### Por qué importa

Sin estas respuestas, dos interpretaciones pueden producir Team Sales y rangos diferentes para la misma red.

### Interpretación técnica segura recomendada

Conservar ventas y ajustes como evidencias separadas y fechadas. No borrar ni alterar un resultado anterior; aplicar la regla oficial mediante una versión nueva o un ajuste trazable.

**Código:** DG-03
**Respuesta final de VISANA:** _________________________________________________
**Otra aclaración:** ___________________________________________________________
**Aprobador y cargo:** _________________________________________________________
**Fecha de aprobación:** ____________________
**Versión:** ____________________  **effectiveFrom:** ____________________
**Evidencia/fuente oficial:** __________________________________________________

## DG-09 — Valor monetario que genera volumen/calificación

**Estado actual:** `PARTIAL_CLIENT_DECISION_REQUIRED` / `NO_APROBADA`.

**Fuente actual:** aclaración verbal atribuida a Catherine del 2026-09-05, conservada en la Working Baseline provisional y el Decision Register.

### Entendimiento actual

El candidato verbal actual es el valor después del descuento y antes de impuestos. En el ejemplo siguiente sería COP 80.000. Este candidato es `PROVISIONAL — NOT APPROVED`.

### Punto no resuelto

VISANA debe seleccionar el monto que contribuye, confirmar si el envío entra y definir cancelaciones, reembolsos y ajustes. También debe indicar si la fórmula es idéntica para volumen personal, Team Sales y calificación.

### Ejemplo concreto

| Componente | Valor |
|---|---:|
| Valor de lista del producto | COP 100.000 |
| Descuento | COP 20.000 |
| Neto antes de impuestos | COP 80.000 |
| Impuesto | COP 15.200 |
| Envío | COP 10.000 |
| Total pagado | COP 105.200 |

### Decisiones seleccionables

**1. ¿Qué monto contribuye a volumen/calificación?**

- [ ] A. COP 100.000: valor de lista.
- [ ] B. COP 80.000: neto después del descuento y antes de impuestos.
- [ ] C. COP 95.200: neto más impuestos, sin envío.
- [ ] D. COP 105.200: total pagado, incluido el envío.
- [ ] E. Otro monto o fórmula: _______________________________________

**2. ¿El envío contribuye?**

- [ ] Sí.
- [ ] No.
- [ ] Sólo bajo estas condiciones: __________________________________

**3. Cancelación o reembolso total**

- [ ] Restar el valor que había contribuido en el período original.
- [ ] Restarlo en el período del ajuste.
- [ ] Aplicar otra regla: ____________________________________________

**4. Reembolso parcial**

- [ ] Restar proporcionalmente el monto reembolsado.
- [ ] Excluir toda la venta.
- [ ] Aplicar otra fórmula: __________________________________________

**5. Alcance de la fórmula**

- [ ] La misma fórmula aplica a volumen personal, Team Sales y calificación.
- [ ] Cada métrica usa una fórmula distinta. Detalle: _________________
- [ ] Otra: _________________________________________________________

### Por qué importa

El mismo pedido puede contribuir COP 80.000, COP 100.000, COP 95.200, COP 105.200 u otro monto, modificando volumen y progreso de rango.

### Interpretación técnica segura recomendada

Conservar cada componente monetario por separado. Aplicar únicamente una fórmula aprobada, versionada y vigente; representar reembolsos mediante ajustes trazables sin reescribir el pasado.

**Código:** DG-09
**Respuesta final de VISANA:** _________________________________________________
**Otra aclaración:** ___________________________________________________________
**Aprobador y cargo:** _________________________________________________________
**Fecha de aprobación:** ____________________
**Versión:** ____________________  **effectiveFrom:** ____________________
**Evidencia/fuente oficial:** __________________________________________________

## DG-11 — Período usado para medir calificación

**Estado actual:** `BLOCKED` / `NO_APROBADA`.

**Fuente actual:** Decision Register, Working Baseline provisional y Sprint 5 Readiness; ninguna fuente selecciona un período canónico.

### Entendimiento actual

No existe un período canónico seleccionado. La vigencia de activación y los cortes de pago de comisiones son conceptos distintos y no determinan automáticamente el período de calificación.

### Punto no resuelto

Se requiere una única regla de período, límites, zona horaria y fecha de entrada en vigor.

### Ejemplo concreto

Una venta ocurre el 30/09/2026 a las 23:59 y otra el 01/10/2026 a las 00:01. VISANA debe indicar en qué período cuenta cada una.

### Decisiones seleccionables

- [ ] A. Mes calendario.
- [ ] B. Período móvil. Evento/día de inicio y duración: ______________
- [ ] C. Período de corte de comisiones. Fechas exactas: ______________
- [ ] D. Otro período definido: ______________________________________

**Zona horaria oficial:** _____________________________________________________
**Regla exacta para inicio y fin:** ____________________________________________

### Por qué importa

Determina qué ventas forman cada acumulado, cuándo se reinicia y contra qué ventana se calcula el rango.

### Interpretación técnica segura recomendada

Como recomendación técnica, no como regla de negocio, puede utilizarse `America/Bogota` si VISANA la confirma. Registrar inicio inclusivo, fin exclusivo, versión y fecha efectiva. No reutilizar cortes de pago sin decisión expresa.

**Código:** DG-11
**Respuesta final de VISANA:** _________________________________________________
**Otra aclaración:** ___________________________________________________________
**Aprobador y cargo:** _________________________________________________________
**Fecha de aprobación:** ____________________
**Versión:** ____________________  **effectiveFrom:** ____________________
**Evidencia/fuente oficial:** __________________________________________________

## DG-18 — Cambio de patrocinador de una persona con red

**Estado actual:** `PARTIAL_CLIENT_DECISION_REQUIRED`; subárbol `OPEN`; regla `NO_APROBADA`.

**Fuente actual:** aclaración verbal provisional del 2026-09-05 y estado seguro de Sprint 4, consolidados en el Decision Register.

### Entendimiento actual

Una aclaración verbal provisional indica que un cambio sería exclusivamente administrativo, con justificación y auditoría. La operación continúa bloqueada y no existe decisión sobre la red descendiente.

### Punto no resuelto

VISANA debe definir quién se mueve, quién autoriza, si la justificación es obligatoria, desde cuándo rige y cómo se preservan genealogía, resultados y comisiones anteriores.

### Ejemplo concreto

```text
María
├─ Pedro
├─ Ana
└─ Carlos
```

Si María cambia de patrocinador, se debe definir qué ocurre con Pedro, Ana y Carlos.

### Decisiones seleccionables

- [ ] A. María y todo su subárbol se mueven.
- [ ] B. Sólo María se mueve.
- [ ] C. El movimiento queda prohibido cuando María ya tiene descendientes.
- [ ] D. Se permite bajo estas condiciones: ___________________________
- [ ] E. Otra: _______________________________________________________

**Quién autoriza:** ____________________________________________________________
**¿La justificación es obligatoria? Sí/No y detalle:** __________________________
**Fecha efectiva del cambio:** _________________________________________________
**Tratamiento de genealogía histórica:** _______________________________________
**Tratamiento de resultados históricos:** ______________________________________
**Tratamiento de comisiones históricas:** ______________________________________

### Por qué importa

El cambio altera la red futura y podría generar una reatribución indebida si no se separa claramente la vigencia nueva de la historia existente.

### Interpretación técnica segura recomendada

Hasta recibir aprobación, mantener el cambio bloqueado. Preservar eventos y resultados anteriores; no asumir recálculo retroactivo. Cualquier tratamiento histórico distinto requiere instrucción oficial explícita y versionada.

**Código:** DG-18
**Respuesta final de VISANA:** _________________________________________________
**Otra aclaración:** ___________________________________________________________
**Aprobador y cargo:** _________________________________________________________
**Fecha de aprobación:** ____________________
**Versión:** ____________________  **effectiveFrom:** ____________________
**Evidencia/fuente oficial:** __________________________________________________

## DG-19 — ¿Quién puede crear y activar una afiliación?

**Estado actual:** `BLOCKED` / `NO_APROBADA`.

**Fuente actual:** SRC-003 y foundation de Sprint 4, según el Decision Register; no existe respuesta oficial sobre autoridad o evento.

### Entendimiento actual

Crear una cuenta, crear la afiliación, asignar patrocinador y activar al afiliado son hechos diferentes. La evidencia actual no define la autoridad ni el evento oficial para cada transición.

### Punto no resuelto

VISANA debe decidir por separado quién puede crear la afiliación y qué evento la activa.

### Ejemplo concreto

Una persona se registra, indica un patrocinador e inicia una compra. Debe quedar claro cuándo obtiene código de afiliado, cuándo aparece en la red, cuándo queda activa y quién autoriza cada paso.

### Decisiones seleccionables

**1. Autoridad para crear la afiliación**

- [ ] A. Usuario por autoservicio.
- [ ] B. Patrocinador o distribuidor.
- [ ] C. Administrador.
- [ ] D. Sistema automatizado.
- [ ] E. Modelo híbrido. Detalle: ____________________________________
- [ ] Otra: _________________________________________________________

**2. Evento que activa la afiliación**

- [ ] A. Registro de la cuenta.
- [ ] B. Inicio del pago.
- [ ] C. Confirmación del pago.
- [ ] D. Aprobación manual.
- [ ] E. Combinación. Detalle: _______________________________________
- [ ] F. Otro: ______________________________________________________

**Controles o documentos requeridos:** _________________________________________
**Momento de asignación de patrocinador y código:** _____________________________

### Por qué importa

Define quién puede incorporarse a la genealogía, quién controla el alta y desde cuándo la persona participa como afiliada.

### Interpretación técnica segura recomendada

Mantener separados cuenta, afiliación y activación; registrar actor, evento, fecha y evidencia. No habilitar autoservicio, aprobación por patrocinador ni automatización hasta que VISANA seleccione el modelo oficial.

**Código:** DG-19
**Respuesta final de VISANA:** _________________________________________________
**Otra aclaración:** ___________________________________________________________
**Aprobador y cargo:** _________________________________________________________
**Fecha de aprobación:** ____________________
**Versión:** ____________________  **effectiveFrom:** ____________________
**Evidencia/fuente oficial:** __________________________________________________

## QTH-F5-01 — Confirmación de umbrales de calificación L1–L8

**Estado actual:** datos técnicamente cargados como `PROVISIONAL`; `NO_APROBADOS`.

**Fuente actual:** Working Baseline provisional y Business Rule Catalog, contrastados con la foundation técnica de Sprint 5.

Los umbrales deben aprobarse expresamente antes de mostrar rango, progreso o siguiente rango en F5. Una confirmación verbal no cambia su estado.

| Nivel | Afiliación requerida | Directos activos | Indirectos | Team Sales COP | Confirmar o corregir |
|---|:---:|---:|---:|---:|---|
| L1 | Sí | 0 | 0 | 0 | |
| L2 | No | 5 | 0 | 3.000.000 | |
| L3 | No | 7 | 25 | 25.000.000 | |
| L4 | No | 9 | 110 | 80.000.000 | |
| L5 | No | 12 | 350 | 250.000.000 | |
| L6 | No | 15 | 1.200 | 1.000.000.000 | |
| L7 | No | 20 | 3.500 | 2.500.000.000 | |
| L8 | No | 25 | 15.000 | 30.000.000.000 | |

### Decisión seleccionable

- [ ] A. Aprobar la tabla completa exactamente como aparece.
- [ ] B. Aprobar con las correcciones escritas en la última columna.
- [ ] C. No aprobar; nueva tabla o regla: ______________________________
- [ ] Otra: _________________________________________________________

**Código:** QTH-F5-01
**Respuesta final de VISANA:** _________________________________________________
**Aprobador y cargo:** _________________________________________________________
**Fecha de aprobación:** ____________________
**Versión:** ____________________  **effectiveFrom:** ____________________
**Evidencia/fuente oficial:** __________________________________________________

## No se requieren para iniciar F5, pero siguen abiertas para módulos futuros

Estas decisiones no forman parte del gate mínimo de F5 y no deben mezclarse con sus bloqueadores. Continúan abiertas para sus módulos correspondientes:

| Tema | Gate relacionado | Módulo futuro afectado |
|---|---|---|
| Pools de distribuidores | DG-06 | Rewards/Compensation |
| Bono de consistencia | DG-06 | Rewards/Compensation |
| Autoridad de aprobación de comisiones | DG-04 | Finanzas/operación |
| Reverso de comisión ya pagada | DG-10 | Finanzas/ledger/pagos |
| Impuestos y retenciones | DG-12 | Finanzas/contabilidad |
| Umbral y regla de envío | DG-13 | Comercio/envíos |
| Proveedor y confirmación real de pago | DG-15 | Integración de pagos |
| Semántica exacta del pool de nivel 8 | DG-09, alcance de compensación | Rewards/Compensation; no confundir con la base monetaria DG-09 que sí bloquea F5 |

Esta separación no aprueba ni posterga indefinidamente las reglas; sólo evita bloquear F5 con decisiones que no determinan su comportamiento de sólo lectura.

## Entrega y aprobación del paquete

**Documento oficial adjunto o referencia:** ____________________________________
**Observaciones transversales:** _______________________________________________
**Responsable VISANA que consolida la respuesta:** ______________________________
**Fecha de entrega:** ____________________
**Versión del paquete respondido:** ____________________________________________

Diligenciar este documento no modifica automáticamente el Decision Register. La respuesta debe reconciliarse campo por campo, conservar la evidencia anterior y clasificarse como `MATCH`, `PARTIAL_MATCH`, `CONFLICT` o `NEW_INFORMATION` antes de promover cualquier regla.
