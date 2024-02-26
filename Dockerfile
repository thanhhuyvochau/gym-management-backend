# Stage 1: Build JAR
FROM maven:3.8.4-openjdk-11-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean install

# Stage 2: Create Docker image
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/Bsmart-0.0.1-SNAPSHOT.jar /app/Bsmart-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=docker","Bsmart-0.0.1-SNAPSHOT.jar"]
