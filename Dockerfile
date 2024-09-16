FROM alpine:3.20.3

RUN apk add --no-cache bash
RUN apk add --no-cache openjdk17

WORKDIR /app

COPY target/projects-0.0.1-SNAPSHOT.jar projects.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${PROFILE:default}", "/app/projects.jar"]
