# Runs the jar the Package stage assembled (gradlePackage), so the image holds
# exactly the tested artifact.
FROM eclipse-temurin:21-jre

ARG APP_VERSION=dev
ENV APP_VERSION=$APP_VERSION

WORKDIR /app
COPY build/libs/catalog-service-*.jar /app/catalog-service.jar

RUN useradd --system --uid 10001 app
USER 10001

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/catalog-service.jar"]
