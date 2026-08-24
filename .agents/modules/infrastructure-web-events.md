# Infrastructure Layer: Web Adapters & Event Bus

## Propósito
Esta fase finaliza la conexión del Bounded Context hacia el mundo exterior a través de "Driving Adapters" (Adaptadores que manejan interacciones *hacia* el sistema). Estos incluyen solicitudes HTTP entrantes y eventos asincrónicos propagados en memoria.

## Arquitectura de Adaptadores

### 1. Spring Event Bus (Asincronismo Limpio)
- **`SpringEventPublisherAdapter`**: Implementa el puerto de salida `DomainEventPublisher` de Commerce. Utiliza `ApplicationEventPublisher` de Spring internamente. Esto permite que el caso de uso `ConfirmOrderPaymentService` lance eventos de dominio sin importar ninguna clase de Spring (Desacoplamiento total).
- **`OrderPaidEventListener`**: Escucha (mediante `@EventListener`) los eventos `OrderPaidEvent` que ocurren en cualquier parte de la JVM. Al detectarlo, inyecta y dispara el orquestador de Compensation (`CalculateCommissionsUseCase`).
- **Beneficio**: Si mañana extraemos Compensation a un Microservicio independiente, solo reemplazamos estos dos adaptadores por productores/consumidores de RabbitMQ/Kafka, **sin tocar una sola línea de código en Dominio o Aplicación**.

### 2. REST Controllers
- **`OrderController`**: El punto de entrada web para Commerce. 
- Mapea la petición HTTP al `ConfirmOrderCommand` y llama al caso de uso.
- **Multitenancy**: Inyecta directamente `@AuthenticationPrincipal Jwt jwt` para extraer las claims. Es un principio de seguridad crítico que la información del Tenant (empresa) o Usuario provenga de un token seguro y cifrado firmado por el servidor de Autorización, no del Payload o URL de un atacante.
- **Regla Estricta**: No retorna entidades `Order`. Si lo hiciese en el futuro, usaría un Response DTO (`OrderResponseDto`).

### 3. Manejo de Errores (RFC 7807)
- **`GlobalExceptionHandler`**: Captura las excepciones nacidas en el corazón del Dominio (como `InsufficientFundsException` o validaciones de estado erróneo) y las transforma en una respuesta estándar HTTP `ProblemDetail` (RFC 7807). 
- Esto impide que los Stack Traces feos y mensajes internos de base de datos se fuguen al front-end, mejorando la seguridad y la experiencia del desarrollador (DX) consumidor de la API.

## Estado de Construcción
- Event Bus conectado inter-dominios exitosamente.
- Capa Web protegida y estandarizada en el manejo de excepciones.
- Pruebas `@WebMvcTest` inyectando JWT Mocks y verificando respuestas HTTP correctas, logrando cobertura robusta a nivel REST.
