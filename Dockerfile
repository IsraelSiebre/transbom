ARG JAVA_VERSION=25

# Compilação da aplicação
FROM maven:3.9-eclipse-temurin-${JAVA_VERSION} AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Execução da aplicação
FROM eclipse-temurin:${JAVA_VERSION}-jre

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
