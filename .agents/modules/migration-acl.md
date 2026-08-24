# Capa Anti-Corrupción (ACL) para Migración de Datos

## Propósito
Este Bounded Context actúa como un escudo o traductor temporal. Cuando un sistema moderno (Clean Architecture + DDD) debe consumir datos de una base de datos legada (Legacy) que carece de reglas estrictas, **nunca debe confiar ciegamente en esos datos**. Si lo hace, la base de datos vieja "corromperá" el modelo nuevo.

## Implementación (Patrón Anti-Corruption Layer)
- **DTOs Desechables (`LegacyUserDto`, `LegacyOrderDto`)**: Representan cómo viene la data desde el SQL viejo. Pueden ser "sucios", con campos nulos o estados inconsistentes.
- **Traducción y Conciliación (`LegacyDataMigrationService`)**: Este servicio intenta tomar un `LegacyDto` e instanciar una **Entidad de Dominio Pura** del sistema nuevo.
- **Fail-Fast Advantage**: Como nuestras Entidades están diseñadas para lanzar excepciones inmediatamente si sus invariantes se violan (ej. `IllegalArgumentException` por rol inválido, o Sponsor inválido), el ACL captura la excepción usando un simple `try-catch`.
- **Conciliation Report**: En lugar de detener la migración cuando un registro falla, la excepción se atrapa y su causa se guarda en un reporte de conciliación. El negocio (Finanzas o Soporte) podrá revisar luego este archivo para arreglar manualmente a los usuarios corruptos de la vieja BD.

## Estado de Construcción
- ACL implementado y probado unitariamente.
- El ACL rechaza data sucia y genera reportes de conciliación en memoria de forma segura.
