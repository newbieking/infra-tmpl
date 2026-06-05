# Infra-Tmpl: Modern Java Infrastructure Service Blueprint

## 1. Overview

A production-grade Java project template that bundles common infrastructure
services behind a unified Spring Boot application. Developers clone this
repo, run `docker compose up`, and immediately have a working stack with
storage, search, cache, messaging, observability, and a relational database —
all wired into a single Spring Boot service with REST endpoints demonstrating
each integration.

## 2. Goals

| # | Goal |
|---|------|
| G1 | Provide a **one-command** local dev environment (`make up`) |
| G2 | Demonstrate **best-practice** integration with each infra component |
| G3 | Ship with **health checks**, **metrics**, and **distributed tracing** out of the box |
| G4 | Include **integration tests** via Testcontainers — no external infra needed for CI |
| G5 | Use **modern Java** (21) and **modern Spring Boot** (3.x) idioms |

## 3. Infrastructure Services

| Service | Technology | Purpose |
|---------|-----------|---------|
| Relational DB | **PostgreSQL 16** | Primary data store |
| Cache | **Redis 7** | Caching, pub/sub, session store |
| Search | **Elasticsearch 8** | Full-text search, analytics |
| Object Storage | **MinIO** | S3-compatible blob/file storage |
| Message Queue | **Apache Kafka 3.7** (KRaft) | Event streaming, async messaging |
| Metrics | **Prometheus + Grafana** | Metrics collection and dashboards |
| Tracing | **OpenTelemetry + Jaeger** | Distributed tracing |

## 4. Java Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 (LTS, virtual threads) |
| Framework | Spring Boot 3.3 |
| Build | Gradle 8.x (Kotlin DSL) |
| Web | Spring Web (REST) |
| ORM | Spring Data JPA + Hibernate |
| Cache | Spring Data Redis (Lettuce) |
| Search | Spring Data Elasticsearch |
| Messaging | Spring Kafka |
| Object Storage | MinIO Java SDK |
| Observability | Micrometer + Prometheus registry, OpenTelemetry |
| Health | Spring Boot Actuator |
| Testing | JUnit 5, Testcontainers, Spring Boot Test |
| API Docs | SpringDoc OpenAPI (Swagger UI) |

## 5. REST API Surface

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

## 6. Docker Compose Services

```
postgres        → port 5432
redis           → port 6379
elasticsearch   → port 9200
minio           → port 9000 (API) / 9001 (Console)
kafka           → port 9092
prometheus      → port 9091
grafana         → port 3000
jaeger          → port 16686 (UI) / 4318 (OTLP HTTP)
```

## 7. Non-Functional Requirements

- All infra configs externalized via `application.yml` + env vars
- Graceful degradation: app starts even if optional services are down
- Structured JSON logging (Logback + Logstash encoder)
- Makefile for common commands (`make up`, `make build`, `make test`)

## 8. Out of Scope

- Authentication / authorization (add your own)
- CI/CD pipeline definitions (template only provides the code)
- Kubernetes manifests (Docker Compose focus for local dev)
