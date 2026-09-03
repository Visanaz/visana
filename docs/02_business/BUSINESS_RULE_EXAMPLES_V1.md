# Ejemplos de reglas de negocio v1

Todos los nombres, personas y valores son sintéticos. Los ejemplos muestran el efecto de una decisión posible; no seleccionan ni implementan una regla.

## Activación

Ana compra COP 200.000 el 15 de agosto. Con “30 días”, estaría activa hasta el 14 de septiembre. Con “mes calendario”, hasta el 31 de agosto. VISANA debe escoger la vigencia y si aplica también a calificación.

## Team Sales

```text
A
├─ B: venta pagada COP 100
│  └─ D: venta pagada COP 100
└─ C: venta pagada COP 100
```

Si Team Sales de A incluye toda su downline, podría ser COP 300. Si incluye sólo directos, COP 200. Si no incluye ventas de B/C/D, COP 0. Antes de aplicar descuentos, devoluciones o impuestos se necesita DG-03 y DG-09.

## Invitación de distribuidor

Una persona invita a un distribuidor que realiza una compra base aprobada de COP 200.000. Con 10% serían COP 20.000; con 3% serían COP 6.000; con 5% COP 10.000. DG-02 debe escoger una regla y la condición de elegibilidad.

## Nivel 8

Dos personas son elegibles y las ventas aplicables del nivel suman COP 1.000.000. Una bolsa 2% tiene COP 20.000 para repartir, por ejemplo COP 10.000 y COP 10.000. Una comisión individual de 2% podría producir COP 20.000 por cada beneficiario. No son equivalentes.

## Premio adicional 1%

Con una base de COP 100.000 en nivel 1, 15% Unilevel es COP 15.000. Si el premio 1% fuera adicional, el total sería COP 16.000. Si describe el mismo beneficio, seguiría siendo COP 15.000. DG-07 debe evitar doble pago.

## Pool

Ventas medibles de COP 26M alcanzan los umbrales 15M y 25M. Un modelo “sólo mayor” usa 1,5%; uno escalonado podría acumular tramos. El documento no decide entre ellos ni define la base/participantes.

## Bloqueo de rama

A patrocina B. B alcanza el mismo rango de A. La regla documental menciona que los pagos y comisiones de la rama se desactivan, pero no define si son todos los beneficios, desde qué fecha o qué sucede si B baja.

## Devolución o reverso

Una compra genera una comisión calculada, luego se devuelve. Antes del pago se podría anular. Después del pago, VISANA debe definir si descuenta el siguiente período, genera saldo negativo o requiere aprobación manual.
