FROM eclipse-temurin:17-jre
EXPOSE 8081
COPY target/gateway-service-config.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
