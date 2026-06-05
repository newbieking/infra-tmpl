# QWEN.md — infra-tmpl

## Project Overview

A production-grade Java project template that bundles common infrastructure services behind a unified Spring Boot application. Developers clone this repo, run `make up`, and immediately have a working stack with PostgreSQL, Redis, Elasticsearch, MinIO, Kafka, Prometheus, Grafana, and Jaeger — all wired into a single Spring Boot service with REST endpoints demonstrating each integration.

- **Language:** Java 17
- **Framework:** Spring Boot 3.3.5
- **Build:** Gradle 8.x (Kotlin DSL) with version catalog (`gradle/libs.versions.toml`)
- **Package:** `com.infra.template`

## Build & Run

| Command | Description |
|---------|-------------|
| `make up` | Start all infrastructure services via Docker Compose |
| `make down` | Stop and remove all infrastructure containers |
| `make build` | Compile the project (`./gradlew build -x test`) |
| `make test` | Run all tests (`./gradlew test`) |
| `make run` | Start the Spring Boot application (`./gradlew bootRun`) |
| `make clean` | Clean build artifacts (`./gradlew clean`) |
| `make logs` | Tail Docker Compose logs |
| `make ps` | List running Docker Compose services |

**Typical workflow:**
```bash
make up          # start infra containers
make run         # start the Spring Boot app (on port 8080)
```

**Windows note:** Use `gradlew.bat` instead of `./gradlew` when running Gradle commands directly.

## Docker Compose Services

| Service | Image | Ports | Purpose |
|---------|-------|-------|---------|
| PostgreSQL | `postgres:16-alpine` | 5432 | Primary data store |
| Redis | `redis:7-alpine` | 6379 | Cache, pub/sub |
| Elasticsearch | `elasticsearch:8.15.3` | 9200 | Full-text search |
| MinIO | `minio/minio:latest` | 9000 (API), 9001 (Console) | S3-compatible object storage |
| Kafka | `apache/kafka:3.7.0` | 9092 | Event streaming (KRaft mode) |
| Prometheus | `prom/prometheus:v2.54.1` | 9091 | Metrics collection |
| Grafana | `grafana/grafana:11.3.0` | 3000 | Metrics dashboards |
| Jaeger | `jaegertracing/all-in-one:1.62.0` | 16686 (UI), 4318 (OTLP) | Distributed tracing |

Compose file: `docker/docker-compose.yml`

## REST API

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/users` | CRUD | JPA + PostgreSQL demo |
| `/api/v1/cache` | PUT/GET/DELETE | Redis cache demo |
| `/api/v1/search` | POST/GET | Elasticsearch index + search demo |
| `/api/v1/storage` | POST/GET | MinIO upload/download demo |
| `/api/v1/events` | POST/GET | Kafka produce/consume demo |
| `/actuator/health` | GET | Health checks for all services |
| `/actuator/prometheus` | GET | Prometheus metrics scrape endpoint |
| `/swagger-ui.html` | GET | Interactive API documentation |

## Architecture & Code Structure

```
src/main/java/com/infra/template/
├── Application.java              # Spring Boot entry point
├── config/                       # Infrastructure bean configs (Redis, Kafka, ES, MinIO, OpenAPI)
├── controller/                   # REST controllers (thin — delegate to services)
├── service/                      # Business logic, one service per infra component
├── repository/                   # Spring Data JPA repositories
├── entity/                       # JPA entities (traditional getters/setters)
├── dto/                          # Data transfer objects (Java records with Jakarta validation)
├── kafka/                        # Kafka producer/consumer services
├── health/                       # Custom health indicators (MinIO, Kafka)
└── metrics/                      # Micrometer/Prometheus config
```

**Key design decisions:**
- **Thin controllers** — controllers delegate to services; no business logic in controllers
- **Service-per-infra** — each infrastructure component gets a dedicated service class
- **Conditional beans** — services use `@ConditionalOnProperty` so the app starts even if a service is unavailable
- **DTOs as records** — immutable, with Jakarta Bean Validation annotations
- **Entities as traditional classes** — JPA entities use getters/setters

## Development Conventions

### Code Style
- **DTOs:** Use Java `record` types with Jakarta validation annotations (`@NotBlank`, `@Email`, etc.)
- **Entities:** Traditional JPA entity classes with getters/setters, no Lombok
- **Controllers:** Constructor injection, `@Tag` annotations for OpenAPI grouping, return `ResponseEntity<T>`
- **Services:** `@Service` + `@Transactional`, constructor injection, `@Transactional(readOnly = true)` for queries
- **Validation:** Jakarta Bean Validation (`jakarta.validation`) on DTOs; `@Valid` on controller parameters

### Testing
- **Framework:** JUnit 5 + Spring Boot Test + MockMvc
- **Infrastructure tests:** Testcontainers (`@Testcontainers` + `@Container`) — no mocking of infra
- **Test profile:** `@ActiveProfiles("test")` with `application-test.yml`
- **Dynamic properties:** Use `@DynamicPropertySource` to wire Testcontainer connection details
- **Unit tests:** Located in `src/test/java/com/infra/template/controller/`
- **Integration tests:** Located in `src/test/java/com/infra/template/integration/`

### Configuration
- **Profiles:** `local` (default, targets Docker Compose services), `test` (Testcontainers)
- **Externalization:** All infra connection details configurable via environment variables with sensible defaults (e.g., `${DB_HOST:localhost}`)
- **Actuator:** Exposes `health`, `info`, `prometheus`, `metrics` endpoints

### Build
- **Version catalog:** All dependency versions managed in `gradle/libs.versions.toml`
- **JVM args:** `-Xmx2g -XX:+UseG1GC` (in `gradle.properties`)
- **Parallel builds** and **Gradle caching** enabled

## Out of Scope

- Authentication / authorization (add your own)
- CI/CD pipeline definitions
- Kubernetes manifests (Docker Compose focus for local dev)
