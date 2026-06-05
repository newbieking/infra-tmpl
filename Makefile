.PHONY: up down build test run clean

up:
	docker compose -f docker/docker-compose.yml up -d

down:
	docker compose -f docker/docker-compose.yml down

build:
	./gradlew build -x test

test:
	./gradlew test

run:
	./gradlew bootRun

clean:
	./gradlew clean

logs:
	docker compose -f docker/docker-compose.yml logs -f

ps:
	docker compose -f docker/docker-compose.yml ps
