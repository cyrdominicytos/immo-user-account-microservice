# Étape de build
FROM maven:3.9.8-eclipse-temurin-21-alpine AS build
WORKDIR /home/immo/user-account
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape de run
FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY --from=build /home/immo/user-account/target/*.jar /app/user-account.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","user-account.jar"]
