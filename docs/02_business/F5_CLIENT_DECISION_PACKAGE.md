# Paquete de decisiones VISANA previo a F5

**Propósito:** obtener respuestas oficiales, claras y verificables antes de mostrar activación, volumen, Team Sales o calificación.
**Instrucción:** VISANA debe marcar opciones, completar cualquier condición adicional y diligenciar responsable, fecha, versión y fuente. Ninguna opción aparece preseleccionada. Los ejemplos son sintéticos.

## DG-01 — ¿Cuándo empieza y termina la activación?

### Entendimiento actual

Existe una aclaración verbal provisional: la activación se dispara al confirmarse el pago, comienza en la fecha y hora de esa confirmación y dura un mes calendario. Ejemplo provisional: 05/09/2026 a las 14:35 → 05/10/2026.

### Qué falta aclarar

- Confirmar oficialmente evento, inicio y duración.
- Definir si vence en el instante exacto o al final del día aniversario.
- Definir qué sucede si hay una recompra antes de que termine la activación vigente.

### Ejemplo de negocio

Un miembro confirma su pago el 05/09/2026 a las 14:35 y recompra el 25/09/2026. Debe quedar claro hasta qué fecha y hora está activo y desde cuándo cuenta la nueva compra.

### Opciones

**Evento e inicio** — marque una:

- [ ] A. Pago confirmado; inicia en la fecha y hora de confirmación.
- [ ] B. Otro evento e inicio: ________________________________________

**Duración** — marque una:

- [ ] A. Un mes calendario desde el inicio.
- [ ] B. Treinta días corridos desde el inicio.
- [ ] C. Hasta el final del mes en que ocurrió la compra.
- [ ] D. Otra: _______________________________________________________

**Momento de vencimiento** — marque una:

- [ ] A. En la misma hora del día aniversario.
- [ ] B. Al finalizar el día aniversario en hora Colombia.
- [ ] C. Otro: _______________________________________________________

**Recompra anticipada** — marque una:

- [ ] A. La nueva vigencia inicia al confirmarse el nuevo pago.
- [ ] B. La nueva vigencia se agrega después de la fecha de vencimiento vigente.
- [ ] C. Se conserva la fecha que resulte más tardía entre ambas reglas.
- [ ] D. Otra: _______________________________________________________

### Por qué importa

Define el estado activo, su fecha de vencimiento y cualquier elegibilidad que dependa de estar activo.

### Interpretación técnica segura recomendada

Hasta recibir la respuesta oficial, no mostrar estado, vencimiento ni cuenta regresiva como dato canónico. Al aprobarse, registrar una versión con fecha de vigencia, límites temporales explícitos y sin recalcular silenciosamente el pasado.

**Aclaración libre de VISANA:** ________________________________________________
**Respuesta final de VISANA:** _________________________________________________
**Aprobado por / cargo:** ______________________________________________________
**Fecha:** ____________________  **Versión/fuente oficial:** ____________________

## DG-03 — ¿Qué ventas forman las Team Sales?

### Entendimiento actual

La aclaración verbal provisional suma las ventas calificables propias y las de todos los descendientes. No define qué convierte una operación en “venta calificable”.

### Qué falta aclarar

- Confirmar qué miembros de la red se incluyen.
- Elegir el momento y estado que hace calificable una venta.
- Definir cancelaciones, reembolsos completos y reembolsos parciales.
- Definir en qué período entra la venta y en cuál entra un ajuste posterior.

### Ejemplo de negocio

A registra COP 100.000; sus descendientes B y C registran COP 200.000 y COP 300.000. La operación de C se cancela y B recibe después un reembolso parcial de COP 50.000. VISANA debe indicar qué valor cuenta para A, para B y en qué período.

### Opciones

**Miembros incluidos** — marque una:

- [ ] A. Sólo ventas propias.
- [ ] B. Propias + descendientes directos.
- [ ] C. Propias + todos los descendientes.
- [ ] D. Otra profundidad o conjunto: __________________________________

**Momento que hace calificable la venta** — marque una:

- [ ] A. Pago confirmado.
- [ ] B. Orden confirmada, aunque el pago aún no esté confirmado.
- [ ] C. Pedido entregado/finalizado.
- [ ] D. Otro estado o combinación: ____________________________________

**Cancelación y reembolso total** — marque una:

- [ ] A. Nunca cuenta; si ya contó, se resta del período original.
- [ ] B. Nunca cuenta; si ya contó, se resta del período en que ocurre el ajuste.
- [ ] C. Otra: _______________________________________________________

**Reembolso parcial, si aplica** — marque una:

- [ ] A. Resta únicamente el valor reembolsado.
- [ ] B. Excluye toda la venta.
- [ ] C. Otra: _______________________________________________________

**Inclusión temporal** — marque una:

- [ ] A. Fecha del evento seleccionado arriba.
- [ ] B. Fecha de la orden.
- [ ] C. Fecha de entrega.
- [ ] D. Otra: _______________________________________________________

### Por qué importa

Sin esta definición, la cifra de Team Sales y cualquier rango que dependa de ella pueden cambiar según una interpretación no aprobada.

### Interpretación técnica segura recomendada

Conservar cada evento y ajuste como evidencia separada, con fecha y trazabilidad; no sobrescribir resultados históricos. La selección de estado, alcance de red y período pertenece a VISANA y no se presume.

**Aclaración libre de VISANA:** ________________________________________________
**Respuesta final de VISANA:** _________________________________________________
**Aprobado por / cargo:** ______________________________________________________
**Fecha:** ____________________  **Versión/fuente oficial:** ____________________

## DG-09 — ¿Qué valor monetario cuenta para volumen y calificación?

### Entendimiento actual

La aclaración verbal provisional usa el valor después de descuentos y antes de impuestos para qualification. No existe fórmula oficial aprobada ni alcance confirmado para todas las métricas.

### Qué falta aclarar

VISANA debe aprobar cuáles componentes suman, restan o se excluyen y cómo afectan cancelaciones y reembolsos.

### Ejemplo de negocio

Producto bruto COP 120.000; descuento COP 20.000; neto antes de impuestos COP 100.000; impuesto COP 19.000; envío COP 10.000; total pagado COP 129.000. Después se reembolsan COP 30.000.

### Tabla de decisión de fórmula

Marque exactamente el tratamiento oficial de cada componente:

| Componente | Valor del ejemplo | Suma | Resta | Se excluye | Regla o aclaración |
|---|---:|:---:|:---:|:---:|---|
| Bruto | COP 120.000 | [ ] | [ ] | [ ] | |
| Descuento | COP 20.000 | [ ] | [ ] | [ ] | |
| Neto antes de impuestos | COP 100.000 | [ ] | [ ] | [ ] | |
| Impuesto | COP 19.000 | [ ] | [ ] | [ ] | |
| Envío | COP 10.000 | [ ] | [ ] | [ ] | |
| Total pagado | COP 129.000 | [ ] | [ ] | [ ] | |
| Reembolso/cancelación | COP 30.000 | [ ] | [ ] | [ ] | |

Seleccione una fórmula o escriba otra:

- [ ] A. Bruto.
- [ ] B. Bruto menos descuento, equivalente al neto antes de impuestos en el ejemplo.
- [ ] C. Total pagado.
- [ ] D. Otra fórmula: ________________________________________________

Indique si la misma fórmula aplica a volumen personal, Team Sales y calificación, o describa una fórmula por cada caso: ________________________________________________

### Por qué importa

El mismo pedido puede representar COP 120.000, COP 100.000, COP 129.000 u otro valor. Esa diferencia cambia volumen y progreso de calificación.

### Interpretación técnica segura recomendada

Mantener los componentes separados y aplicar sólo una fórmula aprobada, versionada y con fecha de vigencia. Los reembolsos deben generar ajustes trazables; no deben borrar ni reinterpretar registros anteriores.

**Aclaración libre de VISANA:** ________________________________________________
**Respuesta final de VISANA:** _________________________________________________
**Aprobado por / cargo:** ______________________________________________________
**Fecha:** ____________________  **Versión/fuente oficial:** ____________________

## DG-11 — ¿Cuál es el período oficial de calificación?

### Entendimiento actual

No hay una regla seleccionada. La duración de la activación no define automáticamente el período de calificación y los cortes de pago de comisiones tampoco lo definen.

### Qué falta aclarar

- Elegir un único modelo de período.
- Definir inicio, fin, zona horaria y tratamiento de ventas exactamente en el límite.
- Definir desde qué fecha entra en vigor y si existen cierres.

### Ejemplo de negocio

Una venta ocurre el 30/09/2026 a las 23:59 y otra el 01/10/2026 a las 00:01, hora Colombia. VISANA debe indicar en qué período cuenta cada una y qué fechas se muestran al afiliado.

### Opciones

- [ ] A. Mes calendario: del primer día al último día de cada mes.
- [ ] B. Ventana mensual móvil: requiere definir desde qué evento o día empieza para cada persona.
- [ ] C. Otro período definido: _______________________________________
- [ ] D. Período por cortes: describir fechas y confirmar expresamente que esos cortes también gobiernan calificación: _______________________________________

**Zona horaria oficial:** ____________________
**Regla para el instante exacto de inicio/fin:** _________________________________

### Por qué importa

Sin período canónico no se puede explicar qué ventas forman un acumulado, cuándo se reinicia ni contra qué ventana se evalúa un rango.

### Interpretación técnica segura recomendada

Registrar inicio inclusivo y fin exclusivo, zona horaria y versión de regla de cada resultado. No reutilizar cortes de pago para calificación salvo decisión expresa de VISANA.

**Aclaración libre de VISANA:** ________________________________________________
**Respuesta final de VISANA:** _________________________________________________
**Aprobado por / cargo:** ______________________________________________________
**Fecha:** ____________________  **Versión/fuente oficial:** ____________________

## DG-18 — ¿Qué ocurre cuando cambia el patrocinador?

### Entendimiento actual

La aclaración verbal provisional indica que el cambio sería sólo administrativo, con justificación y auditoría. La plataforma lo mantiene bloqueado y no hay regla aprobada sobre el subárbol.

### Qué falta aclarar

- Quiénes se mueven.
- Desde cuándo rige el cambio.
- Cómo se consulta la genealogía anterior.
- Si existe algún efecto sobre volumen o resultados financieros históricos.

### Ejemplo de negocio

B está patrocinado por A y tiene como descendientes a D y E. B solicita pasar bajo C. VISANA debe indicar si D y E también se mueven y desde qué fecha.

### Opciones

- [ ] A. B se mueve con todo su subárbol.
- [ ] B. Sólo B se mueve; sus descendientes permanecen donde estaban.
- [ ] C. El movimiento depende de condiciones específicas: ___________________
- [ ] D. No se permite mover a B una vez que tenga descendientes.

**Quién puede autorizar:** _____________________________________________________
**Justificación/evidencia obligatoria:** ________________________________________
**Fecha efectiva del cambio:** _________________________________________________
**Consulta de genealogía histórica:** __________________________________________
**Tratamiento de volumen histórico:** __________________________________________
**Tratamiento financiero histórico:** __________________________________________

### Por qué importa

Un cambio puede alterar quién aparece en cada red y a quién se atribuyen métricas futuras; también puede causar una reescritura indebida del pasado si no se define la vigencia.

### Interpretación técnica segura recomendada

Aplicar el cambio sólo hacia adelante desde una fecha efectiva, conservar el historial y la auditoría, y no recalcular genealogía, volumen ni finanzas pasadas salvo instrucción oficial explícita y versionada. Hasta la aprobación, mantener el cambio bloqueado.

**Aclaración libre de VISANA:** ________________________________________________
**Respuesta final de VISANA:** _________________________________________________
**Aprobado por / cargo:** ______________________________________________________
**Fecha:** ____________________  **Versión/fuente oficial:** ____________________

## DG-19 — ¿Quién puede crear y activar una afiliación?

### Entendimiento actual

Registro de cuenta, creación de afiliación, asignación de patrocinador y activación son hechos distintos. La evidencia actual no define quién autoriza cada uno.

### Qué falta aclarar

- Quién crea formalmente la afiliación.
- Qué evento la activa.
- Si el patrocinador o distribuidor puede iniciar o aprobar el proceso.
- Qué controles y evidencias son obligatorios.

### Ejemplo de negocio

Una persona crea una cuenta, indica un patrocinador y luego confirma una compra. VISANA debe indicar en qué momento obtiene código de afiliado, aparece en la red y queda activa, y quién puede autorizar cada paso.

### Opciones

- [ ] A. Autoservicio después del pago confirmado.
- [ ] B. Creación o activación exclusiva por administración.
- [ ] C. Creación o activación por distribuidor/patrocinador autorizado.
- [ ] D. Aprovisionamiento automático después del pago confirmado.
- [ ] E. Modelo híbrido; describir qué actor realiza cada paso: ________________

**Controles/documentos requeridos:** ___________________________________________
**¿Puede existir afiliación antes de estar activa?:** ___________________________
**¿Cuándo se asigna el patrocinador y el código?:** _____________________________

### Por qué importa

Define quién puede aparecer en la genealogía, quién controla el alta y cuándo una persona puede participar en funciones de afiliado.

### Interpretación técnica segura recomendada

Mantener separados cuenta, afiliación y activación; registrar actor, evento, fecha y evidencia de cada transición. No habilitar autoservicio, aprobación por sponsor ni automatización hasta que VISANA elija el modelo oficial.

**Aclaración libre de VISANA:** ________________________________________________
**Respuesta final de VISANA:** _________________________________________________
**Aprobado por / cargo:** ______________________________________________________
**Fecha:** ____________________  **Versión/fuente oficial:** ____________________

## Cierre del paquete

**Documento oficial adjunto o referencia:** ____________________________________
**Observaciones transversales:** _______________________________________________
**Responsable de consolidación VISANA:** _______________________________________
**Fecha de entrega:** ____________________  **Versión del paquete respondido:** ______________

Una respuesta sólo cambia a `APPROVED` después de ser incorporada mediante el proceso de gobernanza, con evidencia oficial y una versión efectiva. Diligenciar este archivo por sí solo no despliega ni modifica la plataforma.
