# Application Layer: Compensation Engine

## Propósito
Este módulo de Aplicación orquesta el núcleo del negocio. Su labor es "escuchar" u originar a partir del evento del pago de una orden y coordinar las piezas de información esparcidas por todo el sistema (Genealogía, Calificación, Configuraciones) para inyectárselas al Dominio Puro (`UnilevelCompensationCalculatorService`) y, finalmente, guardar las comisiones resultantes.

## Arquitectura de Puertos y Adaptadores

### 1. El Evento de Entrada (`OrderPaidEvent`)
- Define el Input. En una arquitectura orientada a eventos pura, este servicio funcionaría como un "Listener" (por ejemplo, escuchando una cola Kafka o RabbitMQ).
- Contiene los 3 únicos datos que el motor de compensación necesita de la orden original: **Quién compró** (`AffiliateId`), **Qué orden es** (`OrderId`), y **De cuánto fue** (`total`).

### 2. Puertos de Salida (Los Proveedores de Información)
Debido a que el motor de compensación depende de otros Bounded Contexts, la capa de Aplicación define Puertos para obtener esos datos de manera agnóstica. Estos puertos ocultan si la data viene de otra base de datos, de un caché de Redis o de un microservicio externo:
- **`GenealogyProviderPort`**: Retorna el `upline` (la línea de patrocinadores hacia arriba) hasta el límite estipulado (8 niveles).
- **`QualificationProviderPort`**: Retorna la calificación en curso (`AffiliateQualification`) para un afiliado y periodo.
- **`CommissionPlanProviderPort`**: Obtiene el plan dinámico de porcentajes por nivel desde el sistema.
- **`CommissionRepository`**: Guarda el resultado.

### 3. Application Service (Orquestador)
- **`CalculateCommissionsService`**: Implementa el patrón Orchestrator sin acoplar reglas matemáticas de compensación.
  - Recoge las piezas (`upline`, `qualifications`, `plan`).
  - Llama al `UnilevelCompensationCalculatorService` (Capa de Dominio, Fase 5) el cual realiza la aritmética `Money.multiply(Percentage)` pura y protegida.
  - Verifica si hay comisiones generadas, y las delega al Repositorio.
- **Desacoplamiento de Dominio**: El refactoring de la firma en el Servicio de Dominio eliminó la dependencia a la clase entera de `Order` del Dominio Commerce, logrando un código con límites modulares 100% aislados e independientes.

## Estado de Construcción
- Casos de Uso y Puertos implementados puramente en Java (Cero Spring Context).
- Testeado el flujo de orquestación (Mockeando orígenes de datos mediante Mockito) que asegura que las comisiones se calculan y persisten correctamente cuando ocurre una compra calificada.
