ARG APP_MODULE=api-gateway

FROM eclipse-temurin:25-alpine AS builder
ARG APP_MODULE
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY common-events/pom.xml common-events/
COPY api-gateway/pom.xml api-gateway/
COPY appointment-service/pom.xml appointment-service/
COPY auth-service/pom.xml auth-service/
COPY payment-service/pom.xml payment-service/
COPY notification-service/pom.xml notification-service/
COPY user-profile-service/pom.xml user-profile-service/
COPY provider-service/pom.xml provider-service/
COPY chat-service/pom.xml chat-service/
RUN chmod +x mvnw && ./mvnw dependency:go-offline

COPY common-events/src ./common-events/src
COPY api-gateway/src ./api-gateway/src
COPY appointment-service/src ./appointment-service/src
COPY auth-service/src ./auth-service/src
COPY payment-service/src ./payment-service/src
COPY notification-service/src ./notification-service/src
COPY user-profile-service/src ./user-profile-service/src
COPY provider-service/src ./provider-service/src
COPY chat-service/src ./chat-service/src

RUN ./mvnw -DskipTests -pl ${APP_MODULE} -am clean package

FROM eclipse-temurin:25-alpine
ARG APP_MODULE
WORKDIR /app

EXPOSE 8080

COPY --from=builder /app/${APP_MODULE}/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
