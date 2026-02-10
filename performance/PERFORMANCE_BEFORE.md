# Appointments API Performance Review

---

## Data Generation Strategy

Approximately 500,000 appointment records were generated using a dedicated Flyway SQL migration.
PostgreSQL `generate_series` was used to efficiently populate the database with data simulating
a long-living production system state.

---

## Load Testing Setup

Performance testing was conducted using k6 with 100 parallel virtual users (VUs).
All tests were executed against REST APIs related to appointments and patient activity.
Each test ran for 2 minutes with a steady load profile.

---

## Appointments Read Only Performance Testing

This test focused on appointments-related **read operations** only.

The goal of this test was to evaluate the read-side performance of the
appointments domain under realistic concurrent user load
on a database containing approximately **500,000 appointment records**.

### Test scenario:

100 concurrent virtual users executed a production-like read workload with weighted access patterns:
- **~40%** — Visit listing
    - `GET /visits?type=UPCOMING`
    - `GET /visits?type=PAST`
- **~25%** — Laboratory test history
    - `GET /test/history`
- **~15%** — Online consultation details
    - `GET /online-consultations/{id}`
- **~10%** — Active referrals lookup
    - `GET /prescriptions?patientId={id}`
- **~10%** — Other read operations within the appointments domain

All requests were executed with pagination enabled where applicable to simulate real user behavior.

Test duration: **2 minutes**
Concurrency level: **100 virtual users**

### Results:

- **95th percentile response time:** ~6.1 seconds
- **Average response time:** ~4.0 seconds
- **HTTP request failure rate:** ~14%
- **Total requests executed:** ~2,500
- **Throughput:** ~19.7 requests/second

### Endpoint-Level Observations

#### Stable / Functionally Correct Endpoints

- `GET /visits (UPCOMING, PAST)` — consistently returned 200 responses
- `GET /online-consultations/{id}` — acceptable functional behavior (200 / 404)
- `GET /test/history` — **100% functional success**
- `GET /prescriptions` — **100% functional success**

#### Performance Issues

- Despite 100% logical correctness, **overall latency is high**
- p95 consistently exceeds **6 seconds**, which is unacceptable for read-heavy user flows
- Read performance degrades significantly under concurrent load

### Key Findings:

- All read endpoints are now **logically correct and reachable**
- There are **no functional failures** at the application level
- The appointments read model demonstrates **poor scalability**
- High response times indicate:
    - inefficient JPQL queries
    - excessive JOINs and eager fetching
    - missing or suboptimal database indexes
    - lack of read-side optimization

---

## Appointments Lifecycle Performance Testing

This test focused on full appointments lifecycle execution under realistic
production-like load.
Unlike the read-only scenario, this test exercised all read and write operations
across the appointments domain, including creation, updates, state transitions,
and related entities.

Authentication was executed once per virtual user during the setup phase.
Access tokens were reused to ensure that measured performance reflects
business logic and database behavior, not authentication overhead.

The system operated on a database containing approximately
500,000 appointment records, simulating a long-living production state.

### Test scenario:

100 concurrent virtual users executed a mixed workload representing
realistic patient and doctor behavior:
- **~55%** — Read operations
    - `GET /visits?type=UPCOMING`
    - `GET /visits?type=PAST`
    - `GET /test/history`
    - `GET /prescriptions?patientId={id}`
- **~30%** — Create operations
    - `POST /appointments`
    - `POST /online-consultations`
    - `POST /visits`
    - `POST /prescriptions`
- **~10%** — State-changing operations
    - `POST /appointments/{id}/confirm`
    - `PATCH /appointments/{id}/cancel`
    - `PUT /appointments/{id}`
- **~5%** — Delete operations
    - `DELETE /online-consultations/{id}`

All requests were executed with realistic payloads and identifiers
to simulate actual patient and doctor interaction patterns.

Test duration: **2 minutes**
Concurrency level: **100 virtual users**

### Results

- **95th percentile response time:** ~5.1 seconds
- **Average response time:** ~3.2 seconds
- **HTTP request failure rate:** ~14.8%
- **Total requests executed:** ~3,000
- **Throughput:** ~23.5 requests/second

### Endpoint-Level Observations

#### Read Operations

- `GET /visits (UPCOMING, PAST)` — stable and functionally correct
- `GET /test/history` — **100% success rate after deterministic data seeding**
- `GET /prescriptions` — **100% success rate**
- `GET /online-consultations/{id}` — acceptable latency and success rate

#### Write & State-Changing Operations

- Write operations are functionally reachable but introduce significant contention
- Increased latency and failure rate are primarily driven by:
    - concurrent state transitions
    - database write contention
    - lock amplification under mixed read/write load
- Some write requests return expected business errors (4xx / 404),
  which are considered valid outcomes under concurrent load

### Key Findings

- The system is **functionally correct** under full lifecycle load
- Deterministic data seeding eliminated false-negative test failures
- Mixed read/write traffic introduces significant contention
- Latency remains unacceptably high for user-facing operations
- The appointments domain **does not scale to 100 concurrent users**
  without further database and query-level optimizations
