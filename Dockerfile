# Use the official Maven image to build the application
FROM maven:3.8-openjdk-17 as builder
WORKDIR /app
COPY pom.xml .
# Fetch dependencies
RUN mvn dependency:go-offline
COPY src src
# Package the application without running tests to speed up the build
RUN mvn clean package -DskipTests

# Use the official OpenJDK image to run your application
FROM openjdk:17-jre-slim
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
