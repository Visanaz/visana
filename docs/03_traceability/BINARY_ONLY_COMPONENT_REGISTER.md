# Registro de componentes binarios sin fuente equivalente

**Metodo:** se inventariaron `.class` en `target/classes` y se buscaron las 12 rutas Java equivalentes en todos los commits y ramas accesibles con `git log --all` y arboles Git. No se decompilo ni reconstruyo bytecode.

**Resultado Git:** solo existen `main` y `origin/main` accesibles. Ninguna de las 12 rutas fuente aparece en historia accesible. Los commits `e70da1c` y `ca9f3c4` anuncian capas de persistencia, pero sus cambios observables para estos nombres son binarios bajo `target/`, no `.java` equivalentes.

| Clase / paquete binario | Rol esperado (por nombre; no decompilado) | Interfaz o dependencia fuente relacionada | Dependencia Java actual | Fuente / Git | Impacto potencial | Clasificacion |
|---|---|---|---|---|---|---|
| `CommissionPlanJpaEntity` `compensation...persistence` | Entidad de plan [INFERIDO] | `CommissionPlanProviderPort` posible | El calculo exige plan | Fuente ausente; no en Git accesible | Plan efectivo no reproducible | REIMPLEMENTATION_CANDIDATE |
| `CommissionPlanLevelJpaEntity` mismo paquete | Entidad de niveles [INFERIDO] | Igual anterior | Unilevel usa mapa por nivel | Fuente ausente; no en Git accesible | Escala/override no trazable | REIMPLEMENTATION_CANDIDATE |
| `CommissionPlanPersistenceAdapter` mismo paquete | Adaptador de plan [INFERIDO] | `CommissionPlanProviderPort` posible | Bean de calculo exige puerto | Fuente ausente; no en Git accesible | Inyeccion/lectura plan bloqueada | SOURCE_RECOVERY_REQUIRED |
| `CommissionPlanSpringDataRepository` mismo paquete | Repositorio Spring de plan [INFERIDO] | No evidenciado | Ninguna dependencia fuente directa | Fuente ausente; no en Git accesible | Persistencia plan no verificable | REIMPLEMENTATION_CANDIDATE |
| `DistributorVolumeJpaEntity` mismo paquete | Entidad de volumen [INFERIDO] | No evidenciado | Team sales requerido por calificacion | Fuente ausente; no en Git accesible | Datos de volumen no reconstruibles | REIMPLEMENTATION_CANDIDATE |
| `DistributorVolumeSpringDataRepository` mismo paquete | Repositorio de volumen [INFERIDO] | No evidenciado | Ninguna dependencia fuente directa | Fuente ausente; no en Git accesible | Ruta de volumen sin soporte mantenible | REIMPLEMENTATION_CANDIDATE |
| `GenealogyPersistenceAdapter` mismo paquete | Adaptador de genealogia [INFERIDO] | `GenealogyProviderPort` posible | `CalculateCommissionsService` exige upline | Fuente ausente; no en Git accesible | Upline/orden/depth no trazables | SOURCE_RECOVERY_REQUIRED |
| `NetworkNodeJpaEntity` mismo paquete | Entidad de nodo [INFERIDO] | `NetworkNodeRepository` posible | Crear orden/nodo importa repositorio | Fuente ausente; no en Git accesible | Red no reproducible | SOURCE_RECOVERY_REQUIRED |
| `NetworkNodeSpringDataRepository` mismo paquete | Repositorio Spring de nodo [INFERIDO] | `NetworkNodeRepository` posible | Igual anterior | Fuente ausente; no en Git accesible | Persistencia de red no verificable | SOURCE_RECOVERY_REQUIRED |
| `QualificationPersistenceAdapter` mismo paquete | Adaptador de calificacion [INFERIDO] | `QualificationProviderPort` posible | Calculador consulta calificacion | Fuente ausente; no en Git accesible | Regla calificatoria no conectada | SOURCE_RECOVERY_REQUIRED |
| `NetworkNodeRepository` `network.application.port.out` | Puerto de nodo | Puerto mismo nombre | Importado en dos casos de uso y configuracion | Fuente ausente; no en Git accesible | Fuente actual no representa dependencia declarada | SOURCE_RECOVERY_REQUIRED |
| `NetworkNodePersistenceAdapter` `network...persistence` | Adaptador de nodo [INFERIDO] | `NetworkNodeRepository` posible | Dependencia anterior | Fuente ausente; no en Git accesible | Creacion/consulta de red no demostrable | SOURCE_RECOVERY_REQUIRED |

## Regla de recuperacion

`SOURCE_RECOVERY_REQUIRED` significa localizar una fuente autorizada, commit externo, artefacto de entrega o especificacion antes de pretender reutilizar el componente. `REIMPLEMENTATION_CANDIDATE` significa que, si no aparece fuente autorizada, requerira una nueva implementacion aprobada; no autoriza decompilar ni copiar binarios. La presencia del `.class` permite registrar continuidad potencial, no validar comportamiento.
