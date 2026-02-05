FROM gradle:8.10.2-jdk17 AS builder
WORKDIR /app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY src src

RUN chmod +x gradlew \
  && ./gradlew bootJar -x test

FROM amazoncorretto:17-alpine-jdk
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=default \
    JAVA_OPTS=""

COPY --from=builder /app/build/libs/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]