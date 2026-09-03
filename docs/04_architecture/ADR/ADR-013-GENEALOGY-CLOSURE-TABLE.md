# ADR-013 — Closure table para genealogía Plan 3

**Decisión:** PostgreSQL `genealogy_closure` con filas self (`depth=0`) y relación directa separada en `sponsor_relationships`.

Se evaluaron adjacency list con CTE recursivo, materialized path y closure table. Se selecciona closure table porque permite ancestros, descendientes, profundidad y rama por consultas indexadas, conserva profundidad estructural superior a ocho y hace explícita la actualización transaccional. El coste de escritura es aceptable para el monolito y evita infraestructura adicional. `network_members` no calcula calificación, volumen ni compensación.

Re-parenting está bloqueado: no existe una regla VISANA que lo autorice. La tabla tiene unicidad por miembro, self-sponsorship y ciclos se rechazan antes de insertar la proyección closure.
