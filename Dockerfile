# Etapa 1: Construcción (Build)
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
# Copiamos los archivos de Maven primero para aprovechar la caché de Docker
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
# Descargamos dependencias (modo offline)
RUN ./mvnw dependency:go-offline
# Copiamos el código fuente y compilamos
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Etapa 2: Ejecución (Run) - Imagen final más ligera
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copiamos solo el .jar compilado desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar
# Exponemos el puerto estándar de Cloud Run
EXPOSE 8080
# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
