FROM eclipse-temurin:17-jdk-noble
WORKDIR /app
ARG JAR_FILE
COPY ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]