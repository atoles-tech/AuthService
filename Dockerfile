FROM eclipse-temurin:21-jdk-alpine

COPY /target/auth_service-0.0.1-SNAPSHOT.jar /auth-service/auth-service.jar

WORKDIR /auth-service

EXPOSE 8082

ENTRYPOINT [ "java","-jar","auth-service.jar", "--spring.profiles.active=docker" ]