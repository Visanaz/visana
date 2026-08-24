# Bounded Context: Network & Genealogy

## Propósito
Este módulo es la médula espinal del sistema multinivel (Visana). Representa el Domain Model para la gestión de la red de afiliados, protegiendo las reglas jerárquicas (upline, downline) y el estado de la membresía.

## Entidades y Value Objects Implementados

Siguiendo el principio de pureza del Dominio, estas clases están completamente desacopladas de Spring, JPA o frameworks de persistencia (cero `@Entity`, `@Data`, etc.).

### 1. Value Objects de Identidad
- **`AffiliateId` y `SponsorId`**: Encapsulan un UUID para identificar de forma segura al nodo y a su patrocinador, respectivamente. Garantizan el formato correcto en tiempo de construcción (fail-fast validation) y protegen contra la inyección de tipos primarios (evitando mezclar un ID de base de datos entero con un UUID o intercambiar IDs entre Sponsor y Affiliate inadvertidamente).
- Reutiliza el `TenantId` del Core Domain para proteger y aislar la red de forma Multi-Tenant.

### 2. Enums de Estado y Rol
- **`NetworkRole`**: Define la capacidad operativa y de negocio en la red (`ADMIN`, `SOCIO`, `AFILIADO`, `DISTRIBUIDOR`), alineado con la auditoría de negocio.
- **`NodeStatus`**: Define explícitamente el estado del nodo (`ACTIVO`, `INACTIVO`).

### 3. Entidad Raíz: `GenealogyNode`
- **Descripción**: La entidad principal (Aggregate Root) que representa un nodo en el árbol de red.
- **Protección de Reglas de Negocio (Invariantes)**:
  - **Auto-Patrocinio**: El constructor y el método modificador impiden (lanzando `IllegalArgumentException`) que un `AffiliateId` sea igual a su `SponsorId`, cortocircuitando cualquier bucle de auto-patrocinio antes de tocar la base de datos.
  - **Raíz del Árbol (Root Node)**: Existe un Factory Method especial `createRoot()` que permite un `SponsorId` nulo y estado activo por defecto para el fundador, bloqueando cambios posteriores de sponsor (`changeSponsor` lanza `IllegalStateException` si el nodo es raíz).
  - **Límites de Memoria**: La entidad intencionalmente omite colecciones (`List<GenealogyNode> children`) para evitar la carga en memoria de grafos masivos y lazy loading exceptions (N+1). El recorrido del árbol de Genealogía (ancestors/descendants) será manejado de manera optimizada (ej. Nested Sets, CTEs en PostgreSQL/MySQL) en la capa de Infraestructura y proyecciones de dominio.
  - **Máquina de Estados Simple**: Encapsula métodos explícitos (`activate()`, `deactivate()`, `changeSponsor()`) con sus respectivas validaciones lógicas, prohibiendo transiciones redundantes.

## Estado de Construcción
- Capas de Dominio creadas en `com.visana.erp.network.domain.model`.
- Entidades y VOs probados con JUnit 5 validando reglas jerárquicas (evitar auto-patrocinio, mutaciones ilegales).
- Cobertura estricta (> 90%) en validación de creación e invariantes.

---
**Próximos Pasos**: La siguiente fase deberá interactuar con este Dominio para proyectar el "Upline" (línea ascendente) o "Downline" (línea descendente) en base a eventos del sistema (como una afiliación o recompra).
