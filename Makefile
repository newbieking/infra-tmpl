.PHONY: up down build test run clean logs ps build-all

# ==================== Infrastructure ====================

up:
	docker compose -f docker/docker-compose.yml up -d postgres redis elasticsearch minio kafka prometheus grafana jaeger nacos seata sentinel

down:
	docker compose -f docker/docker-compose.yml down

logs:
	docker compose -f docker/docker-compose.yml logs -f

ps:
	docker compose -f docker/docker-compose.yml ps

# ==================== Build ====================

build:
	./gradlew build -x test

build-all:
	./gradlew build -x test
	docker compose -f docker/docker-compose.yml build

test:
	./gradlew test

clean:
	./gradlew clean

# ==================== Run Services ====================

run-gateway:
	./gradlew :infra-gateway:bootRun

run-user:
	./gradlew :infra-user:bootRun

run-order:
	./gradlew :infra-order:bootRun

run-search:
	./gradlew :infra-search:bootRun

run-storage:
	./gradlew :infra-storage:bootRun

run-event:
	./gradlew :infra-event:bootRun

# Run all services (in separate terminals)
run-all:
	@echo "Start infrastructure first: make up"
	@echo "Then run each service in separate terminals:"
	@echo "  make run-gateway   (port 8080)"
	@echo "  make run-user      (port 8081)"
	@echo "  make run-order     (port 8082)"
	@echo "  make run-search    (port 8083)"
	@echo "  make run-storage   (port 8084)"
	@echo "  make run-event     (port 8085)"

# ==================== Docker Compose (full stack) ====================

up-all:
	docker compose -f docker/docker-compose.yml up -d --build
