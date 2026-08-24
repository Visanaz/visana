# Bounded Context: Qualification Engine

## Propósito
El motor de calificación es la capa matemática pura responsable de determinar si un afiliado cumple las condiciones (candados) de red y volumen para acceder a beneficios o niveles dentro del plan de compensación. 

Su diseño está fuertemente impulsado por el principio "Configuration over Hard-code". Ninguna regla de nivel (ej. "Nivel 3 requiere 25 millones y 7 directos") y ningún parámetro conflictivo (ej. "Activación = 30 días y 200k") está escrito en piedra (código fuente). En su lugar, el Dominio expone una interfaz agnóstica que evalúa reglas dinámicas provenientes del exterior.

## Arquitectura del Dominio

### 1. Value Objects Inmutables
- **`NetworkRequirement`**: Define métricas jerárquicas puras (`minDirects`, `minIndirects`).
- **`VolumeRequirement`**: Define candados financieros (`Money minTeamSales`). Al reutilizar el VO `Money` de la Fase 1, heredamos automáticamente la protección contra saldos negativos y errores de coma flotante.
- **`AffiliateMetrics`**: Un recolector de la "foto actual" del afiliado (`directs`, `indirects`, `teamSales`). Agrupa el snapshot de avance.

### 2. Entidad Parametrizada (`LevelQualificationRule`)
Es el corazón de la evaluación dinámica. 
- Almacena de manera cohesiva el nivel destino (`level`), y sus candados asociados (`NetworkRequirement`, `VolumeRequirement`).
- **Resolución de Reglas (Dynamic Parametrization)**: En lugar de tener una clase monstruosa llena de `if (level == 3) { check 25M } else if (level == 4) { check 100M }`, esta entidad posee la lógica pura `isSatisfiedBy(actualDirects, actualIndirects, actualVolume)`. La base de datos, en la capa de infraestructura, inyectará N de estas reglas como una lista, permitiendo alterar el Plan de Compensación sin redeplegar código.

### 3. Aggregate Root (`AffiliateQualification`)
Consolida el estado contable y calificatorio de un Afiliado (`AffiliateId`, `TenantId`) en un Mes particular (`Period`).
- Bloquea invariantes críticas, por ejemplo: _Un afiliado jamás puede ser marcado como calificado si su flag de activación es falso_ (Fail-fast en el constructor).

### 4. Domain Service Puro (`QualificationEvaluatorService`)
- No usa `@Service` de Spring. Es un servicio puro de DDD.
- **Resolución de Conflictos de Negocio (AUD-001)**: El documento de auditoría señaló que la duración de la activación era incierta (29 vs 30 días). Este servicio aborda el conflicto delegando la decisión hacia el exterior. Su método `evaluateActivation` no "asume" 30 días ni $200.000 COP; en su lugar, requiere que le pasen por parámetro `requiredActivationAmount` y `activationDurationDays`, logrando que la resolución del conflicto sea un simple cambio en configuración / Base de Datos, sin obligar al desarrollador a tocar el core o generar bugs futuros.
- **Iteración Segura de Niveles**: Toma una lista de reglas (`LevelQualificationRule`), las evalúa en base a las métricas del usuario usando Streams funcionales, y retorna el nivel más alto cumplido (o 0 si no califica).

## Estado de Construcción
- Archivos generados bajo `com.visana.erp.qualification.domain.model` y `.../service`.
- Cobertura de pruebas (JUnit 5) cubriendo 100% de la lógica aritmética, las fechas límite (activación vencida / vigente), y el algoritmo de recorrido de reglas (encontrando el nivel más alto de una lista desordenada o sin cumplir ninguna regla).
