# Business Rules Baseline v1 - borrador para aprobación VISANA

**Estado:** `DRAFT_FOR_VISANA_APPROVAL`

**Propósito:** convertir la evidencia actual en decisiones de negocio explícitas. No aprueba, implementa ni reemplaza una regla existente.
**Fuente funcional primaria:** `VISANA SAS - Plan de Compensacion-3.pdf`, páginas 1-4 (SRC-003), contrastada con el catálogo, SQL y código documentados. Cuando hay diferencia, se conserva como pregunta; no se escoge una respuesta.

## Cómo usar este documento

Cada regla se responde en la sesión indicada. Para cerrar una regla VISANA debe seleccionar una opción o escribir otra, identificar a quien aprueba y fechar la decisión. `PENDIENTE` no es una respuesta aprobada.

## Decision Session A - Qualification & Volume

### BR-BASE-001 - Activación y recompra (DG-01, DG-11)
- **Qué dice el documento actual:** primera compra mínima COP 200.000; recompra mensual mínima COP 100.000. También dice activación por el mes de compra y duración de 30 días.
- **Qué está claro:** los dos importes. SQL también registra 200.000, 100.000 y 30 días.
- **Qué está ambiguo:** vigencia, fecha de inicio, tratamiento de compra a mitad de mes y si el período es igual para activación, calificación y bonos.
- **Pregunta a VISANA:** una compra el día 15, ¿hasta cuándo mantiene activa a la persona?
- **Opciones de decisión:** A. 30 días desde compra. B. último día del mes calendario. C. período de corte de pago. D. otra regla documentada.
- **Ejemplo sencillo:** compra de COP 200.000 el 15 de agosto: A vence el 14 de septiembre; B vence el 31 de agosto.
- **Impacto de la decisión:** determina quién puede contar como activo, calificar y recibir beneficios futuros.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-002 - Definición de Team Sales (DG-03)
- **Qué dice el documento actual:** los niveles N2-N8 exigen ventas en equipo; no define qué ventas entran.
- **Qué está claro:** existen umbrales documentados, desde COP 3M para N2 hasta COP 30.000M para N8.
- **Qué está ambiguo:** ventas propias, directos, downline, profundidad, pago confirmado, cancelaciones, devoluciones, descuentos, envío, impuestos y ajustes.
- **Pregunta a VISANA:** ¿qué ventas exactas forman las Team Sales de cada persona?
- **Opciones de decisión:** A. sólo propias. B. propias y directos. C. toda la downline. D. otra definición con profundidad y estados explícitos.
- **Ejemplo sencillo:** A patrocina B y C; B patrocina D. Si B, C y D venden COP 100 cada uno, VISANA debe indicar cuánto de COP 300 cuenta para A y cuánto para B.
- **Impacto de la decisión:** bloquea volumen y calificación; no se implementará una venta calificable sin ella.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-003 - Base de cálculo (DG-09)
- **Qué dice el documento actual:** los porcentajes se expresan sobre ventas, pero no precisa la base monetaria común.
- **Qué está claro:** existen porcentajes documentados y configuraciones SQL potenciales.
- **Qué está ambiguo:** precio lista, subtotal pagado, descuentos, envío, impuestos, devoluciones y puntos PV/CV si VISANA decidiera usarlos.
- **Pregunta a VISANA:** ¿sobre qué monto se calcula cada incentivo o comisión?
- **Opciones de decisión:** A. precio lista. B. subtotal realmente pagado. C. subtotal tras descuentos y sin envío/impuestos. D. PV/CV aprobado. E. otra base.
- **Ejemplo sencillo:** lista COP 120.000, descuento COP 20.000, envío COP 10.000 e impuesto COP 19.000: VISANA debe elegir cuál monto es la base.
- **Impacto de la decisión:** evita cálculos dobles, diferencias por descuento y conflictos entre plan, rol y configuración.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-004 - Perfiles de negocio por persona (DG-17)
- **Qué dice el documento actual:** distingue distribuidores, afiliados, clientes preferentes y consumidores; el sistema técnico no decide su cardinalidad de negocio.
- **Qué está claro:** una identidad técnica no equivale automáticamente a afiliado o distribuidor.
- **Qué está ambiguo:** si una persona puede ser cliente, afiliado y distribuidor al mismo tiempo.
- **Pregunta a VISANA:** ¿qué combinaciones de perfiles puede tener una misma persona/cuenta?
- **Opciones de decisión:** A. perfiles excluyentes. B. varios perfiles simultáneos. C. sólo combinaciones de una matriz aprobada. D. otra.
- **Ejemplo sencillo:** una persona compra para sí y luego recomienda a otra: VISANA debe indicar si conserva cliente y suma afiliado, o cambia de perfil.
- **Impacto de la decisión:** condiciona acceso, ownership, afiliación y reglas de beneficios.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-005 - Cambio de patrocinador (DG-18)
- **Qué dice el documento actual:** no define cambio de patrocinador. La foundation técnica lo rechaza de forma segura mientras no exista decisión.
- **Qué está claro:** no debe cambiarse una relación sin autorización de negocio y sin conservar trazabilidad.
- **Qué está ambiguo:** si se permite, quién lo autoriza, desde cuándo rige y qué sucede con historia, volumen y beneficios anteriores.
- **Pregunta a VISANA:** ¿se puede cambiar patrocinador después de afiliarse?
- **Opciones de decisión:** A. nunca. B. sólo antes de primera compra/activación. C. sólo administración con justificación. D. bajo condiciones específicas. E. otra.
- **Ejemplo sencillo:** B fue patrocinado por A y ya hizo compras; solicita pasar con C. VISANA debe indicar si afecta sólo futuro o también historia previa.
- **Impacto de la decisión:** preserva genealogía, auditoría y futuras métricas de red.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-006 - Autoridad de afiliación (DG-19)
- **Qué dice el documento actual:** afiliación otorga código y acceso; no especifica quién ni qué evento crea formalmente al afiliado.
- **Qué está claro:** cuenta creada, afiliado, activado y calificado son estados distintos.
- **Qué está ambiguo:** evento de nacimiento de una afiliación y controles de aprobación.
- **Pregunta a VISANA:** ¿cuándo nace formalmente un afiliado VISANA?
- **Opciones de decisión:** A. al registrarse. B. primera compra COP 200.000. C. aprobación administrativa. D. pago confirmado. E. combinación aprobada.
- **Ejemplo sencillo:** una cuenta se registra sin compra: VISANA debe indicar si puede tener patrocinador, código y aparecer en red.
- **Impacto de la decisión:** condiciona provisioning, genealogía y control de acceso.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

## Decision Session B - Rewards / Compensation

### BR-BASE-007 - Invitación de distribuidor (DG-02)
- **Qué dice el documento actual:** PDF: 10%. Configuración/auditoría: 3% hasta cinco activos y 5% desde seis.
- **Qué está claro:** las fuentes difieren y no hay una regla Java ejecutable que resuelva la diferencia.
- **Qué está ambiguo:** porcentaje oficial, elegibilidad, período y base.
- **Pregunta a VISANA:** ¿cuál es la regla oficial de invitación de distribuidor?
- **Opciones de decisión:** A. 10% fijo. B. 3%/5% por condición. C. configurable por plan/rol con condiciones exactas. D. otra.
- **Ejemplo sencillo:** un distribuidor invita a su sexto activo; VISANA debe indicar el porcentaje y sobre qué monto.
- **Impacto de la decisión:** evita un incentivo contractual contradictorio.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-008 - Semántica del nivel 8
- **Qué dice el documento actual:** PDF describe bolsa de 2% repartida equitativamente; Java histórico modela 2% individual por ancestro.
- **Qué está claro:** ambas semánticas no son equivalentes.
- **Qué está ambiguo:** beneficiarios, elegibilidad, bolsa, período y método de reparto.
- **Pregunta a VISANA:** ¿el 2% del nivel 8 es individual o una bolsa global?
- **Opciones de decisión:** A. comisión individual por descendiente de nivel 8. B. bolsa global 2% entre elegibles. C. otra.
- **Ejemplo sencillo:** dos personas activan nivel 8 sobre COP 1.000.000: una bolsa de COP 20.000 repartida no equivale a COP 20.000 para cada una.
- **Impacto de la decisión:** bloquea cualquier compensación de nivel 8.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-009 - Premio adicional 1% por niveles 1-7 (DG-07)
- **Qué dice el documento actual:** PDF indica 1% mensual por cada nivel 1-7, total teórico 7%.
- **Qué está claro:** no hay fuente que lo conecte con la escala Unilevel.
- **Qué está ambiguo:** si suma a 15/10/5/4/3/2/1/2, quién participa, base y condiciones.
- **Pregunta a VISANA:** ¿el 1% es adicional, otra descripción del mismo beneficio o condicionado?
- **Opciones de decisión:** A. adicional. B. mismo beneficio descrito de otra forma. C. condicionado. D. otra.
- **Ejemplo sencillo:** venta base COP 100.000 en L1: 15% Unilevel más 1% adicional serían COP 16.000; VISANA debe confirmar o descartar esa suma.
- **Impacto de la decisión:** previene doble pago.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-010 - Pools de distribuidor (DG-06)
- **Qué dice el documento actual:** 15M→1%, 25M→1,5%, 45M→2%, 100M→2,5%; el PDF indica reparto equitativo.
- **Qué está claro:** los montos y porcentajes están documentados; existen semillas de configuración.
- **Qué está ambiguo:** base, participantes, escalonamiento y si aplica sólo el mayor umbral.
- **Pregunta a VISANA:** ¿qué ventas miden cada pool y cómo se aplican los umbrales?
- **Opciones de decisión:** A. ventas personales. B. equipo individual. C. ventas globales de distribuidores. D. otra; y elegir acumulado/escalonado o sólo mayor alcanzado.
- **Ejemplo sencillo:** con 26M, la regla puede otorgar 1,5% solamente o 1% + 1,5%; VISANA debe definirlo.
- **Impacto de la decisión:** bloquea distribución, elegibilidad y trazabilidad financiera.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-011 - Consistencia (DG-06)
- **Qué dice el documento actual:** 6 meses sosteniendo/aumentando → 0,5%; 12 meses → 1%.
- **Qué está claro:** períodos y porcentajes documentados.
- **Qué está ambiguo:** referencia de comparación, reinicio de racha, disminución y si 1% reemplaza o suma 0,5%.
- **Pregunta a VISANA:** ¿qué significa sostener o aumentar y cómo opera la racha?
- **Opciones de decisión:** A. comparar mes anterior. B. comparar mes base. C. otra regla documentada; definir reinicio y acumulación.
- **Ejemplo sencillo:** una secuencia 100, 100, 99 en el mes 3: VISANA debe decidir si la racha se rompe.
- **Impacto de la decisión:** evita incentivos inconsistentes entre meses.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-012 - Restricción de rama por rango (DG-08)
- **Qué dice el documento actual:** cuando un afiliado de una rama alcanza o supera al upline, se desactivan pagos de esa rama.
- **Qué está claro:** hay una restricción documental.
- **Qué está ambiguo:** qué pagos se bloquean, cuándo inicia, reactivación e historia.
- **Pregunta a VISANA:** ¿qué deja de cobrar A cuando B, de su rama, alcanza el mismo rango?
- **Opciones de decisión:** A. toda rama. B. ciertos niveles. C. sólo premio de rango. D. desde período siguiente. E. otra.
- **Ejemplo sencillo:** A patrocina B; B iguala rango de A. VISANA debe indicar el efecto inmediato y si se revierte cuando B baja.
- **Impacto de la decisión:** condiciona elegibilidad de beneficios futuros.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

## Decision Session C - Finance & Operations

### BR-BASE-013 - Aprobación de comisiones (DG-04)
- **Qué dice el documento actual:** existen estados históricos pending/approved/paid y un modelo Java aislado calculated/reversed.
- **Qué está claro:** cálculo, aprobación, pagable y pagado son etapas diferentes.
- **Qué está ambiguo:** actor autorizador, controles y momento de aprobación.
- **Pregunta a VISANA:** ¿quién convierte una comisión calculada en aprobada/pagable?
- **Opciones de decisión:** SYSTEM AUTO, ACCOUNTING, ADMIN, MANAGEMENT, DUAL APPROVAL u otra.
- **Ejemplo sencillo:** una comisión calculada hoy: VISANA debe indicar quién puede aprobarla y bajo qué evidencia.
- **Impacto de la decisión:** bloquea ledger, liquidación y auditoría financiera.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-014 - Ciclo de orden de pago (DG-05)
- **Qué dice el documento actual:** PDF menciona cortes 1-15/pago 25 y 16-fin/pago 10; no define ciclo completo.
- **Qué está claro:** hay dos ventanas documentadas.
- **Qué está ambiguo:** responsables y transición entre creada, revisada, aprobada, pagada y fallida/cancelada.
- **Pregunta a VISANA:** ¿cuál es el ciclo oficial de una orden de pago y quién responde en cada estado?
- **Opciones de decisión:** A. flujo mínimo propuesto. B. flujo contable existente entregado por VISANA. C. otro.
- **Ejemplo sencillo:** un pago falla el día 25: VISANA debe indicar si se reintenta, cancela o pasa al siguiente corte.
- **Impacto de la decisión:** permite conciliación y comunicación de estados.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-015 - Cancelaciones, devoluciones y reversos (DG-10)
- **Qué dice el documento actual:** existe reverso aislado, sin definición integral para venta, volumen, calificación y pago.
- **Qué está claro:** una cancelación antes y después de pagar no produce el mismo efecto operativo.
- **Qué está ambiguo:** anulación, descuento futuro, saldo negativo y aprobación manual.
- **Pregunta a VISANA:** ¿qué sucede antes y después de pagar una comisión asociada a una compra cancelada, devuelta o reversada?
- **Opciones de decisión:** A. anular. B. descontar período siguiente. C. saldo negativo. D. aprobación manual. E. combinación.
- **Ejemplo sencillo:** se devuelve una compra cuya comisión ya fue pagada: VISANA debe elegir cómo se recupera el valor.
- **Impacto de la decisión:** protege integridad financiera e histórica.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-016 - Impuestos y retenciones (DG-12)
- **Qué dice el documento actual:** PDF menciona Retención en la Fuente y Rete-ICA.
- **Qué está claro:** las reglas deben provenir de Contabilidad y ser configurables/versionadas.
- **Qué está ambiguo:** tasas, base, sujetos, vigencia y responsable.
- **Pregunta a VISANA:** ¿qué reglas oficiales de impuestos/retenciones entrega Contabilidad?
- **Opciones de decisión:** A. entregar matriz contable vigente. B. definir proveedor contable responsable. C. otra.
- **Ejemplo sencillo:** comisión bruta COP 100.000: VISANA debe indicar retención aplicable, fecha y monto neto esperado.
- **Impacto de la decisión:** bloquea pago neto y cumplimiento.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-017 - Envío por más de tres productos (DG-13)
- **Qué dice el documento actual:** la compañía asume el envío por compras de más de tres productos.
- **Qué está claro:** existe el beneficio documental.
- **Qué está ambiguo:** tres unidades, SKUs, líneas, destinos, topes y excepciones.
- **Pregunta a VISANA:** ¿qué significa “más de 3 productos” y dónde aplica?
- **Opciones de decisión:** A. unidades. B. SKUs distintos. C. líneas de pedido. D. otra; definir destinos/topes/excepciones.
- **Ejemplo sencillo:** dos unidades de SKU A y dos de SKU B: son cuatro unidades, dos SKUs y dos líneas; VISANA debe definir el resultado.
- **Impacto de la decisión:** bloquea regla comercial y operación de envío.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

### BR-BASE-018 - Proveedor de pagos (DG-15)
- **Qué dice el documento actual:** no hay proveedor real; el comportamiento actual es simulación.
- **Qué está claro:** la selección comercial debe preceder la integración.
- **Qué está ambiguo:** proveedor, checkout, webhooks, reembolsos, sandbox y conciliación.
- **Pregunta a VISANA:** ¿qué pasarela/proveedor tiene o quiere contratar VISANA?
- **Opciones de decisión:** A. proveedor ya contratado. B. proceso de selección comercial. C. otro.
- **Ejemplo sencillo:** para confirmar un pago, VISANA debe indicar si el proveedor envía notificación verificable y cómo se reconcilia.
- **Impacto de la decisión:** bloquea confirmación financiera real.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `NEEDS_BUSINESS_DECISION`.

## Decision Session D - Product & Platform

### BR-BASE-019 - Proveedor de identidad (DG-14)
- **Qué dice el documento actual:** Keycloak/OIDC funciona desacoplado mediante PlatformActor.
- **Qué está claro:** no debe confundirse identidad técnica con perfil de negocio.
- **Qué está ambiguo:** decisión comercial/operativa de conservar o migrar proveedor.
- **Pregunta a VISANA:** ¿qué proveedor de identidad se mantendrá para el lanzamiento?
- **Opciones de decisión:** A. mantener Keycloak. B. migrar a Identity Platform/Firebase Auth. C. decidir después con plan de migración. D. otra.
- **Ejemplo sencillo:** si se migra, VISANA debe aprobar cómo se conservarán accesos e identidades existentes.
- **Impacto de la decisión:** afecta operación, costos y plan de migración; no bloquea la foundation de Sprint 5.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `TECHNICAL_DECISION`.

### BR-BASE-020 - Fuente frontend (DG-16)
- **Qué dice el documento actual:** no hay fuente frontend recuperable en este clon.
- **Qué está claro:** no se debe construir una interfaz sin alcance/fuente aprobados.
- **Qué está ambiguo:** existencia de repositorio actual y estrategia de UI.
- **Pregunta a VISANA:** ¿existe una fuente frontend que será entregada?
- **Opciones de decisión:** A. sí, se entrega. B. no existe fuente recuperable. C. se descarta y se desarrolla nuevo Angular. D. otra.
- **Ejemplo sencillo:** si existe un repositorio, VISANA debe indicar ubicación, versión y responsable de entrega.
- **Impacto de la decisión:** bloquea UI/go-live, no Sprint 5 técnico.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `TECHNICAL_DECISION`.

## RULES CURRENTLY DOCUMENTED BUT NOT YET CODE-AUTHORIZED

### BR-BASE-021 - Escala Unilevel L1-L7
- **Qué dice el documento actual:** 15%, 10%, 5%, 4%, 3%, 2% y 1% para niveles 1-7.
- **Qué está claro:** la secuencia figura en el PDF y en la configuración base; no decide overrides por rol ni la base de cálculo.
- **Qué está ambiguo:** aplica DG-09 antes de cualquier cálculo efectivo.
- **Pregunta a VISANA:** ¿aprueba esta escala documental como baseline sujeto a la base y precedencia que decida en DG-09?
- **Opciones de decisión:** A. aprobar como está. B. aprobar con una excepción documentada. C. reemplazar por tabla oficial.
- **Ejemplo sencillo:** una venta con base aprobada de COP 100.000 produce L1 COP 15.000 si la persona es elegible.
- **Impacto de la decisión:** fija la tabla documental, no autoriza cálculo ni pago.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `READY_FOR_APPROVAL`.

### BR-BASE-022 - Umbrales N1-N8 de red
- **Qué dice el documento actual:** N2 inicia con 5 directos y COP 3M; N8 llega a 25 directos, 15.000 indirectos y COP 30.000M; tabla completa en el PDF p3.
- **Qué está claro:** los valores documentales de la tabla.
- **Qué está ambiguo:** Team Sales, período, activación y venta calificable se resuelven en Session A.
- **Pregunta a VISANA:** ¿aprueba la tabla de umbrales como baseline sujeto a las definiciones de Team Sales y período?
- **Opciones de decisión:** A. aprobar tabla. B. aprobar con cambios entregados en tabla oficial. C. reemplazar por otra tabla.
- **Ejemplo sencillo:** N2 exige 5 directos activos y COP 3M; el significado de activo y ventas está pendiente.
- **Impacto de la decisión:** conserva la referencia de negocio sin autorizar implementación prematura.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `READY_FOR_APPROVAL`.

### BR-BASE-023 - Cortes y fechas de pago documentadas
- **Qué dice el documento actual:** corte 1-15 para pago el 25; corte 16-fin de mes para pago el 10.
- **Qué está claro:** las dos fechas y ventanas del documento.
- **Qué está ambiguo:** aprobación, fallos, reintentos, feriados y lifecycle de DG-05.
- **Pregunta a VISANA:** ¿aprueba estas ventanas como baseline documental sujeto al ciclo de pago de DG-05?
- **Opciones de decisión:** A. aprobar. B. aprobar con calendario operativo adjunto. C. reemplazar por calendario oficial.
- **Ejemplo sencillo:** un evento del día 16 entra en la segunda ventana; VISANA debe confirmar pago el 10 siguiente.
- **Impacto de la decisión:** documenta calendario, no autoriza pagos.
- **Decisión seleccionada:** PENDIENTE. **Fecha de aprobación:** PENDIENTE. **Aprobado por:** PENDIENTE. **Estado:** `READY_FOR_APPROVAL`.

## Resumen para control

| Estado | Cantidad |
|---|---:|
| NEEDS_BUSINESS_DECISION | 18 |
| TECHNICAL_DECISION | 2 |
| READY_FOR_APPROVAL | 3 |
| APPROVED | 0 |
| **Total** | **23** |

Sprint 5 no inicia hasta que VISANA apruebe, como mínimo, DG-01, DG-03, DG-09, DG-11, DG-17, DG-18 y DG-19.
