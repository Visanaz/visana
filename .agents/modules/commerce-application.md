# Application Layer: Commerce

## Propósito
Este módulo asciende un peldaño en la Clean Architecture, construyendo la Capa de Aplicación para el comercio. Su propósito es orquestar los casos de uso (interacciones) sin incluir reglas de negocio puras y aislando al dominio completamente de la infraestructura externa (JPA, Controladores HTTP).

## Arquitectura de Puertos y Adaptadores (Hexagonal)

### 1. DTOs y Comandos (Inputs)
- **`ConfirmOrderCommand`**: Es un DTO inmutable (`record`) que encapsula la intención del usuario. Desacopla la API HTTP del core. El Controller web simplemente mapeará el JSON a este comando.
- **`OrderPaidEvent`**: Representa un Evento de Dominio puro. Transporta lo que sucedió (`OrderId`, quién compró y cuánto) pero no depende de librerías de mensajería específicas (como Kafka o RabbitMQ).

### 2. Puertos de Entrada y Salida (Interfaces Puras)
- **`ConfirmOrderPaymentUseCase` (Port in)**: Define el contrato hacia el exterior (el "qué" hace este módulo). 
- **`OrderRepository` (Port out)**: Una interfaz de Java Pura. **No extiende de `JpaRepository` ni importa clases de Spring Data**. Esto garantiza que si mañana migramos de MySQL (Legacy) a MongoDB o DynamoDB, el caso de uso no se entera ni se rompe.
- **`DomainEventPublisher` (Port out)**: Permite que este caso de uso notifique al resto del sistema que el pago fue exitoso, fomentando la arquitectura orientada a eventos.

### 3. Application Service (El Orquestador)
- **`ConfirmOrderPaymentService`**: Implementa el caso de uso.
  - Orquesta el flujo: 1) Trae la Orden de DB -> 2) Ejecuta Dominio (`order.confirmPayment()`) -> 3) Guarda DB -> 4) Publica el Evento.
  - Al aislar este flujo, podemos probar el caso de uso entero sin necesidad de levantar un contenedor de Spring o una Base de Datos en Memoria.

## Estado de Construcción
- Capas de puertos (`in`, `out`) y servicios creados.
- `DomainEvent` centralizado en Core.
- Tests (JUnit + Mockito) inyectando dependencias falsas (`mock()`) para comprobar la interacción entre puertos y dominio, logrando cobertura robusta a nivel orquestación.
