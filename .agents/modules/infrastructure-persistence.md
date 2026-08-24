# Infrastructure Layer: Persistence Adapters

## Propósito
En esta fase hemos descendido a la capa más externa de la arquitectura (Infraestructura). Aquí implementamos los "Adaptadores de Salida" para la persistencia, permitiendo que nuestro Dominio puro pueda guardar y recuperar su estado de una base de datos relacional (MySQL) a través de Spring Data JPA, sin comprometer las reglas del Dominio.

## Arquitectura de Adaptadores

### 1. Entidades JPA vs. Entidades de Dominio
Por el principio de Clean Architecture, **NO anotamos nuestras entidades de dominio con `@Entity` o `@Table`**. 
En su lugar, creamos representaciones "sucias" o específicas de la infraestructura (`OrderJpaEntity`, `CommissionJpaEntity`). 
- **Ventaja**: La estructura de la tabla puede cambiar (por rendimiento, normalización, etc.) sin impactar al código de dominio.
- **Multitenancy**: Todas las tablas principales incluyen la columna `empresa_id`, cumpliendo con las directrices de diseño.
- **IDs**: Usamos `VARCHAR(36)` para mapear nuestros `UUID` de dominio de forma agnóstica.

### 2. El Patrón Mapper
Las clases `OrderMapper` y `CommissionMapper` actúan como traductores. 
- Al guardar (`toJpaEntity`), extraen los valores puros de los Value Objects (ej. `.value().toString()`) y los asignan a las entidades JPA.
- Al recuperar (`toDomainEntity`), toman los tipos primitivos de la BD (String, BigDecimal) y "reconstruyen" las ricas Entidades de Dominio (inyectando sus validaciones fail-fast en el proceso de instanciación).
- Para el caso de `Commission`, se habilitó un método `reconstruct` en el Dominio para poder levantar el historial inmutable desde la persistencia sin lanzar eventos colaterales.

### 3. Spring Data Repositories y Adapters
- **Repositories**: Interfaces de Spring (`extends JpaRepository`) que solo conocen de `JpaEntity`.
- **Adapters**: Implementan los Puertos definidos en la Capa de Aplicación (`OrderRepository`, `CommissionRepository`). Son clases `@Component` que inyectan el repositorio de Spring y el Mapper. El resto de la aplicación habla con el Adapter sin saber que detrás hay SQL o JPA.

### 4. Migraciones (Flyway)
La creación de los esquemas relacionales se maneja con control de versiones en SQL (`V1__init_commerce_and_compensation.sql`). Se utilizaron convenciones `snake_case` y escalares `DECIMAL(19,4)` para precisión monetaria, en perfecta alineación con nuestro `Money` `BigDecimal`.

## Estado de Construcción
- Adaptadores de Persistencia para Commerce y Compensation completados.
- Base de datos versionada con Flyway.
- Listo para realizar las pruebas de Integración con Testcontainers y JPA.
