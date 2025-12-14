# Actuator Endpoints: Monitoring and Diagnostics (Medfast API)

---

## 1. Overview and Rationale

The Spring Boot Actuator integrated into the Medfast API to enhance **observability**, **diagnostics**,
and **operational monitoring**.
This integration provides standardized health checks, metrics, and system information necessary for various monitoring tools (e.g., Kubernetes, Prometheus).

---

## 2. Core Actuator Endpoints and Usage

The base path for all management endpoints is **`/actuator`**.

| Path | Purpose | Key Usage | Access Level                                      |
| :--- | :--- | :--- |:--------------------------------------------------|
| `/actuator/health` | **Overall Health Status** and aggregated component checks (DB, Disk, Liveness/Readiness). | CI/CD, Detailed Diagnosis | Varies by Environment (See Sec 3)                 |
| `/actuator/health/liveness` | **Liveness Probe**. Determines if the application is fundamentally running. | Kubernetes Restarts | Varies by Environment (See Sec 3)                              |
| `/actuator/health/readiness` | **Readiness Probe**. Determines if the application is ready to handle traffic. | Kubernetes Load Balancing | Varies by Environment (See Sec 3)                              |
| `/actuator/prometheus` | Exports all application and JVM metrics in Prometheus format. | Prometheus Scraping | Varies by Environment (See Sec 3)                 |
| `/actuator/metrics` | Lists available metrics and allows querying specific metrics for diagnosis. | Local Debugging | Varies by Environment (See Sec 3)                 |
| `/actuator/info` | Application metadata (Version, Git Commit, Environment). | Debugging| **Public** **Restricted** in Stage          |
| `/actuator/env` | All environment variables and configurations. | Configuration Audit | Varies by Environment (See Sec 3) |

---

## 3. Access Restrictions and Environment Configuration

Access is controlled via **Spring Security** and environment-specific Actuator configuration profiles (`application-*.yml`).

### 3.1. Public Access (Monitoring Tools)

The following endpoints are **Publicly Accessible** without authentication in **all environments** to support infrastructure monitoring:
* `/actuator/info`

### 3.2. Environment-Specific Exposure Rules

| Environment | Endpoints Requiring **Authentication**                   | Endpoints **Excluded/Disabled** (Restricted) |
| :--- |:---------------------------------------------------------|:---------------------------------------------|
| **`dev`** | None.                                                    | None.                                        |
| **`stage`** | `health` (details), `metrics`, `env`, `prometheus`,      | `info` **Restricted**                        |


---

## 4. Operational Diagnostics and Information Criteria

### 4.1. Health Checks

* The primary `/actuator/health` endpoint aggregates the status of all available components.
* **Database Check:** Actuator automatically includes the status of the PostgreSQL data source (e.g., `db: { "status": "UP" }`).
* **AC:** `/actuator/health` returns correct status and includes DB checks.

### 4.2. Build Metadata (`/actuator/info`)

The `/actuator/info` endpoint provides essential context:
* **`info.build`**: Exposes the application **version** and build time.
* **`info.git`**: Exposes the **Git commit hash** for direct code traceability.
* **`/actuator/info`** exposes build metadata (version, commit hash).
