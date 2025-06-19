FROM gradle:8.14.2-jdk17 AS builder
WORKDIR /app
COPY build.gradle .
COPY settings.gradle .
COPY gradle ./gradle
RUN gradle build --no-daemon --build-cache -x test || true
COPY . .
RUN gradle build --no-daemon --build-cache -x test

FROM openjdk:17-oracle
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]