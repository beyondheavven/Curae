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
- `k8s/` — Kubernetes manifests (Deployments, Services, HPA, Ingress, infrastructure, kustomization)
- `.github/workflows/` — CI (build + test on PRs) and CD (images to GHCR + deploy)
- `postman/` — API collection and environment
- `docker-compose.yml`, `Dockerfile` — containerized build/run
- `ACTUATOR.md`, `TESTING_CONVENTIONS.md`, `CONTRIBUTING.md` — additional docs

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

## Kubernetes

`k8s/` contains a kustomization that deploys the whole stack into the `curae` namespace. All application services are exposed internally as `ClusterIP`; the only outside entry point is an **nginx Ingress** on 80/443.

| External host | Backend | What you get |
| --- | --- | --- |
| `api.curae.local` | `api-gateway:8080` | REST API, Swagger `/api/swagger-ui/index.html`, OpenAPI `/v3/api-docs` |
| `grafana.curae.local` | `grafana:3000` | Dashboards (admin / `GF_ADMIN_PASSWORD`) |
| `prometheus.curae.local` | `prometheus:9090` | Targets, alerts |
| `loki.curae.local` | `loki:3100` | Log API |
| `rabbitmq.curae.local` | `rabbitmq:15672` | RabbitMQ management UI |
| `mail.curae.local` | `mailpit:8025` | Mailpit UI (local SMTP catcher) |

8080/3000/9090/3100 are **in-cluster** ports. From outside use the Ingress hosts on 80/443. Replace `curae.local` with your real domain and add TLS in the Ingress (cert-manager annotation) for HTTPS.

Prerequisites: an nginx Ingress Controller, a default StorageClass, and two objects created from local files (not committed):

```bash
kubectl create secret generic curae-secrets --from-env-file=.env -n curae
kubectl create configmap grafana-dashboards --from-file=monitoring/grafana/dashboards -n curae
```

`curae-secrets` must contain `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`, `JWT_SECRET`, `GF_ADMIN_PASSWORD`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`, `REDIS_PASSWORD`, plus the mail/Stripe keys used by notification and payment services.

If the GHCR package is private, add `imagePullSecrets` to the service Deployments (create it with `kubectl -n curae create secret docker-registry ghcr ...`).

Deploy and inspect:

```bash
kubectl apply -k k8s
kubectl -n curae get pods,svc,ingress,hpa
kubectl -n curae rollout status deployment/api-gateway
```

Each service starts with 2 replicas and an HPA (`min 2 / max 5`, CPU 70%, memory 80%). Stateful infrastructure (Postgres, Kafka, RabbitMQ, Redis, Loki, Prometheus, Grafana) runs 1 replica with PVCs. Probes use `/actuator/health/readiness` and `/actuator/health/liveness` (see `ACTUATOR.md`).

### Local Kubernetes on Docker Desktop

Use the `docker-desktop` context; `minikube` is not used for this stack. Docker Desktop's LoadBalancer mapping on port 80 can intermittently drop connections (`ERR_EMPTY_RESPONSE` for `swagger-initializer.js`), so keep a stable port-forward to the Ingress controller:

```bash
kubectl config use-context docker-desktop
kubectl -n ingress-nginx port-forward deployment/ingress-nginx-controller 18080:80 --address 127.0.0.1
```

Then open `http://api.curae.local:18080/api/swagger-ui/index.html`. The aggregated spec is `/v3/api-docs`; the gateway-only spec is `/v3/api-docs/gateway`.

Map the Ingress hosts to `127.0.0.1` in `C:\Windows\System32\drivers\etc\hosts` first:

```text
127.0.0.1 api.curae.local
127.0.0.1 grafana.curae.local
127.0.0.1 prometheus.curae.local
127.0.0.1 loki.curae.local
127.0.0.1 rabbitmq.curae.local
127.0.0.1 mail.curae.local
```

Verify with `kubectl -n curae get pods` and `kubectl -n curae rollout status deployment/api-gateway`. HPA metrics require `metrics-server`; on Docker Desktop it may need `--kubelet-insecure-tls`.

For a GUI, install Headlamp and select cluster `docker-desktop`, namespace `curae`:

```bash
winget install Headlamp.Headlamp
```

Headlamp reads `~/.kube/config`, so no token is needed locally. `mailpit` receives SMTP on `mailpit:1025`; its UI is `mail.curae.local`.

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

## CI/CD and Git Workflow

- `ci.yml` runs `./mvnw -B -ntp verify` on pull requests to `main` and on pushes to feature branches.
- `cd.yml` runs on pushes to `main`: builds 8 images, pushes them to GHCR, applies `k8s/`, pins each Deployment to the commit SHA and waits for rollout.
- Deploy needs repository secret `KUBE_CONFIG` (base64-encoded kubeconfig). Without it the deploy steps are skipped.
- Direct pushes to `main` are not allowed — see `CONTRIBUTING.md` for the branch/PR workflow and branch-protection settings.

## Additional Documentation

- `ACTUATOR.md` — actuator endpoints and diagnostics
- `TESTING_CONVENTIONS.md` — testing guidelines
- `CONTRIBUTING.md` — branching, PR and release workflow