# Neighborly Backend

Spring Boot REST API for Neighborly, a community volunteer matching app.

## Run locally

Requirements: Java 17+ and Maven 3.9+.

For a quick demo with sample opportunities and an in-memory database:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

For PostgreSQL, configure `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD`, then run:

```powershell
mvn spring-boot:run
```

The API listens on port 8080. `GET /api/health` is the health endpoint.
