# Curae

Microservices-based healthcare backend platform.

## Overview

Curae is split into domain services behind an API Gateway. Services communicate synchronously over HTTP and asynchronously via Kafka (domain events, `common-events`) and RabbitMQ/STOMP (chat).

## Architecture

| Service | Port | Responsibility | Database / Messaging |
| --- | ---: | --- | --- |
| `api-gateway` | 8080 | Entry point: routing, JWT validation, CORS, aggregated OpenAPI/Swagger | - |
| `auth-service` | 8081 | Authentication, users, registration, countries, email/password flows | `curae_auth`, Kafka |
| `appointment-service` | 8082 | Appointments, visits, tests/lab results, referrals, consultations, statistics | `curae_appointment`, Kafka |
| `payment-service` | 8083 | Payments/purchases, Stripe checkout & webhooks | `curae_payment`, Kafka, Stripe |
| `notification-service` | 8084 | Notifications and email delivery | `curae_notification`, Kafka, SMTP |
| `user-profile-service` | 8085 | User/patient profiles | `curae_profile`, Kafka |
| `provider-service` | 8086 | Doctors, clinics, specializations, provided services, feedback | `curae_provider`, Kafka |
| `chat-service` | 8087 | Patient–doctor chat (REST + WebSocket/STOMP) | `curae_chat`, RabbitMQ |
| `common-events` | - | Shared library: events, response wrappers, common DTOs | - |

**Infrastructure (Docker Compose):** PostgreSQL, Kafka, RabbitMQ, Redis, Stripe CLI, Prometheus, Grafana, Loki, Promtail.

## Repository Structure

- `api-gateway/`, `auth-service/`, `appointment-service/`, `payment-service/`, `notification-service/`, `user-profile-service/`, `provider-service/`, `chat-service/` — service modules
- `common-events/` — shared contracts/utilities
- `scripts/init-databases.sql` — creates service databases on first PostgreSQL init
- `*/src/main/resources/db/migration/` — Flyway migrations per service
- `monitoring/` — Prometheus, Loki, Promtail, Grafana provisioning & dashboards
- `postman/` — API collection and environment
- `docker-compose.yml`, `Dockerfile` — containerized build/run
- `ACTUATOR.md`, `TESTING_CONVENTIONS.md` — additional docs

## Prerequisites

- Docker & Docker Compose
- (For local non-container builds) JDK 25 + Maven Wrapper (`mvnw.cmd` / `./mvnw`)

## Configuration

Create a `.env` file in the project root (used by `docker-compose.yml`). Required/commonly used variables:

```env
# PostgreSQL
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_DB=
POSTGRES_PORT=5432

# Service databases
AUTH_DB_NAME=curae_auth
APPOINTMENT_DB_NAME=curae_appointment
PAYMENT_DB_NAME=curae_payment
NOTIFICATION_DB_NAME=curae_notification
PROFILE_DB_NAME=curae_profile
PROVIDER_DB_NAME=curae_provider
CHAT_DB_NAME=curae_chat

# Security
JWT_SECRET=
EMAIL_VERIFICATION_TTL_MS=3600000
CORS_ALLOWED_ORIGINS=http://localhost:8000,http://localhost:3000,http://localhost:8080

# Messaging / cache
RABBITMQ_USERNAME=
RABBITMQ_PASSWORD=
REDIS_PASSWORD=

# Mail (notification-service)
MAIL_HOST=
MAIL_PORT=
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true

# Stripe (payment-service / stripe-cli)
STRIPE_PUBLIC_KEY=
STRIPE_SECRET_KEY=
WEBHOOK_SECRET_KEY=
PAYMENT_SUCCESS_URL=
PAYMENT_CANCEL_URL=

# Monitoring
GF_ADMIN_PASSWORD=
```

> Check `docker-compose.yml` and each service `application.yml` for the full/default set of variables.

## Quick Start (Docker)

```bash
docker compose up -d --build
```

- PostgreSQL initializes databases from `scripts/init-databases.sql` **only on first startup** of the `pgdata` volume.
- To recreate from scratch: `docker compose down -v` then start again.

Useful endpoints:

| URL | Purpose |
| --- | --- |
| `http://localhost:8080/api/swagger-ui.html` | Gateway Swagger UI |
| `http://localhost:8080/v3/api-docs` | Aggregated OpenAPI (gateway) |
| `http://localhost:3000` | Grafana (`admin` / `GF_ADMIN_PASSWORD`) |
| `http://localhost:9090` | Prometheus |
| `http://localhost:3100` | Loki API |

Service ports: gateway `8080`, auth `8081`, appointment `8082`, payment `8083`, notification `8084`, profile `8085`, provider `8086`, chat `8087`.

## Local Build

```bash
# Windows (PowerShell)
.\mvnw.cmd -DskipTests clean compile

# Build specific service with dependencies
.\mvnw.cmd -DskipTests -pl chat-service -am clean compile
```

## API & Gateway

- All external traffic should go through `api-gateway` (`:8080`).
- Gateway routes requests by path to downstream services (auth, appointments/visits/referrals/statistics, profiles/patients, providers/clinics/feedbacks, payments/webhooks, notifications, chats, WebSocket `/ws/**`).
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`, aggregated spec: `http://localhost:8080/v3/api-docs`.
- Individual services expose their own Springdoc endpoints and actuator on their ports.

## Databases & Migrations

- One PostgreSQL instance, separate database per service (`curae_auth`, `curae_appointment`, `curae_payment`, `curae_notification`, `curae_profile`, `curae_provider`, `curae_chat`).
- Schema changes are managed by Flyway per service (`*/src/main/resources/db/migration/`, `ddl-auto: validate`).

## Messaging

- **Kafka:** asynchronous domain/integration events (shared in `common-events`, produced/consumed by services).
- **RabbitMQ:** chat message fanout/transport for `chat-service` (WebSocket/STOMP).

## Observability

- **Actuator:** services expose `health`, `info`, `prometheus` (see `ACTUATOR.md`).
- **Prometheus:** scrapes `/actuator/prometheus` from gateway and all services (`monitoring/prometheus/prometheus.yml`).
- **Loki + Promtail:** Promtail collects Docker container logs (`curae-*`) and ships to Loki (`monitoring/promtail/promtail-config.yml`).
- **Grafana:** provisioned datasources and dashboards in `monitoring/grafana/`.

## Testing

Run module/all tests with Maven Wrapper:

```bash
.\mvnw.cmd test
.\mvnw.cmd -pl auth-service -am test
```

Most service modules include `src/test`; `common-events` and `chat-service` currently have no test sources. See `TESTING_CONVENTIONS.md` for conventions.

## API Testing (Postman)

1. Import `postman/curae-api-postman-collection.json` and `postman/dev-postman-env.json`.
2. Select the **Dev** environment.
3. Run the collection (consider adding a small delay, e.g. 200 ms, to avoid token/race issues during rotation).

## Additional Documentation

- `ACTUATOR.md` — actuator endpoints and diagnostics
- `TESTING_CONVENTIONS.md` — testing guidelines