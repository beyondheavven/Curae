# Actuator & Diagnostics (Curae)

## Overview

Curae services use Spring Boot Actuator for health checks and Prometheus metrics. In containerized environments each service exposes actuator endpoints on its own HTTP port.

## Common Endpoints

Base path: `/actuator`

| Endpoint | Purpose |
| --- | --- |
| `/actuator/health` | Health status (and components, where enabled) |
| `/actuator/info` | Application/build info (if configured) |
| `/actuator/prometheus` | Metrics in Prometheus exposition format |

Exposure is configured per service in `*/src/main/resources/application.yml` under `management.endpoints.web.exposure.include` (typically `health,info,prometheus`). Public access through the API Gateway depends on gateway public paths/security rules (`api-gateway/src/main/resources/application.yml`).

## Services & Ports

| Service | Base URL (local/compose) | Prometheus metrics |
| --- | --- | --- |
| api-gateway | `http://localhost:8080` | `http://localhost:8080/actuator/prometheus` |
| auth-service | `http://localhost:8081` | `http://localhost:8081/actuator/prometheus` |
| appointment-service | `http://localhost:8082` | `http://localhost:8082/actuator/prometheus` |
| payment-service | `http://localhost:8083` | `http://localhost:8083/actuator/prometheus` |
| notification-service | `http://localhost:8084` | `http://localhost:8084/actuator/prometheus` |
| user-profile-service | `http://localhost:8085` | `http://localhost:8085/actuator/prometheus` |
| provider-service | `http://localhost:8086` | `http://localhost:8086/actuator/prometheus` |
| chat-service | `http://localhost:8087` | `http://localhost:8087/actuator/prometheus` |

## Prometheus & Grafana

- Scrape targets are defined in `monitoring/prometheus/prometheus.yml` (Docker service names/ports).
- Prometheus UI: `http://localhost:9090` (Targets page shows scrape status).
- Grafana: `http://localhost:3000` with provisioned Prometheus/Loki datasources and dashboards in `monitoring/grafana/`.
- Logs are collected by Promtail from containers matching `curae-*` and stored in Loki.

## Runtime Diagnostics

To inspect/change log levels at runtime (where actuator loggers endpoint is exposed), use `/actuator/loggers` on the target service, for example:

```bash
curl http://localhost:8081/actuator/loggers
curl -i -X POST -H "Content-Type: application/json" \
  -d '{"configuredLevel":"DEBUG"}' \
  http://localhost:8081/actuator/loggers/com.vention.curae
```

> Adjust host/port to the specific service. Availability of individual actuator endpoints depends on exposure and security configuration.