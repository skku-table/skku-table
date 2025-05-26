FROM eclipse-temurin:21.0.6_7-jdk-noble
WORKDIR /app
ARG JAR_FILE
COPY ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]