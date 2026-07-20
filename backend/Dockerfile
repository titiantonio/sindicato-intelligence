FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

RUN groupadd --system app \
    && useradd --system --gid app app \
    && mkdir -p /app/logs/errors /app/data \
    && chown -R app:app /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

USER app

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
