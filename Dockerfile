FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copia pom.xml primero para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "app.jar"]



#docker build -t luci4sanchez2007/hackathon-backend:latest .
#docker push luci4sanchez2007/hackathon-backend:latest
