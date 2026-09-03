# Genealogy Model — Sprint 4

`sponsor_relationships` representa patrocinador → miembro y una relación inicial activa. `genealogy_closure(ancestor, descendant, depth)` materializa ancestros y descendientes, incluyendo self row a profundidad cero. La rama es un miembro directo y todos sus descendientes.

No existe tope estructural: más de ocho niveles se preservan. Ocho es sólo una posible profundidad futura de compensación, que permanece sin implementar. Las asignaciones de sponsor y el mantenimiento closure son una sola transacción; duplicado, auto-patrocinio, ciclo y re-parenting se rechazan.

Migración futura: legacy affiliate → reconciliación → perfil de negocio → network member → enlace actor opcional. Debe reportar huérfanos, ciclos, duplicados, self references e IDs inválidos antes de cualquier import.
