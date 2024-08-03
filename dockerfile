FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean install

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/SpringAPI-1.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]