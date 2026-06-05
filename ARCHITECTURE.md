# Architecture Plan

## Project Structure

```
infra-tmpl/
├── SPEC.md
├── ARCHITECTURE.md
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/wrapper/
├── gradlew / gradlew.bat
├── Makefile
├── .gitignore
├── docker/
│   ├── docker-compose.yml
│   ├── docker-compose.override.yml
│   ├── prometheus/prometheus.yml
│   └── grafana/provisioning/
│       ├── datasources/datasource.yml
│       └── dashboards/
├── src/
│   ├── main/
│   │   ├── java/com/infra/template/
│   │   │   ├── Application.java
│   │   │   ├── config/
│   │   │   │   ├── RedisConfig.java
│   │   │   │   ├── KafkaConfig.java
│   │   │   │   ├── ElasticsearchConfig.java
│   │   │   │   ├── MinioConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── UserController.java
│   │   │   │   ├── CacheController.java
│   │   │   │   ├── SearchController.java
│   │   │   │   ├── StorageController.java
│   │   │   │   └── EventController.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   ├── CacheService.java
│   │   │   │   ├── SearchService.java
│   │   │   │   ├── StorageService.java
│   │   │   │   └── EventService.java
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java
│   │   │   ├── entity/
│   │   │   │   └── User.java
│   │   │   ├── dto/
│   │   │   │   ├── UserDto.java
│   │   │   │   ├── CacheEntry.java
│   │   │   │   ├── SearchRequest.java
│   │   │   │   ├── SearchResult.java
│   │   │   │   └── EventMessage.java
│   │   │   ├── kafka/
│   │   │   │   ├── KafkaProducer.java
│   │   │   │   └── KafkaConsumer.java
│   │   │   ├── health/
│   │   │   │   ├── MinioHealthIndicator.java
│   │   │   │   └── KafkaHealthIndicator.java
│   │   │   └── metrics/
│   │   │       └── MetricsConfig.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-test.yml
│   │       └── logback-spring.xml
│   └── test/
│       └── java/com/infra/template/
│           ├── ApplicationTests.java
│           ├── controller/
│           │   └── UserControllerTest.java
│           └── integration/
│               └── InfrastructureIntegrationTest.java
```

## Design Decisions

1. **Thin controllers** — controllers delegate to services; no business logic in controllers
2. **Service-per-infra** — each infrastructure component gets a dedicated service class
3. **Conditional beans** — services use `@ConditionalOnProperty` so the app starts even if a service is unavailable
4. **Testcontainers** — integration tests spin up real Docker containers; no mocking of infra
5. **Gradle Kotlin DSL** — type-safe build scripts with version catalogs in `gradle/libs.versions.toml`
6. **Spring profiles** — `local` (docker compose), `test` (testcontainers), default = local
