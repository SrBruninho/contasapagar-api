# Stage 1: Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run stage
FROM openjdk:17-jdk-alpine AS production
WORKDIR /app
COPY --from=build /app/target/contasapagar-api-0.0.1-SNAPSHOT.jar /app/contasapagar-api.jar

RUN chmod +x ./contasapagar-api.jar

EXPOSE 8080

ENTRYPOINT  ["java", "-jar", "/app/contasapagar-api.jar"]
