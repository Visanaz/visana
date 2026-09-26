# Package the executable JAR produced and verified by CI; never copy local target.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY .ci-artifact/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
