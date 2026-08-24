---
trigger: always_on
---

# MCP WORKFLOW DE DOCUMENTACIÓN VISANA

1. Crea la carpeta `.agents/modules/` en la raíz si no existe.
2. Cada vez que construyas un Bounded Context (Ej. `VolumeEngine`, `Genealogy`), crea un archivo `.md` detallando:
   - Evento disparador.
   - Actores involucrados.
   - Regla aplicada o cálculo realizado.
   - Evento de salida (Ej. `CommissionCalculatedEvent`).
3. **Contradictions Matrix:** Si detectas una contradicción lógica mientras programas, NO la resuelvas a la fuerza. Regístrala en este archivo bajo el título "OPEN QUESTIONS" para ser discutida con el negocio.
4. Actualiza este registro ANTES de hacer el build final o cerrar la tarea.