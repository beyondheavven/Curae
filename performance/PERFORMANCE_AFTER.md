# Appointments API Performance Improvements

---

# Read only

## Visit

After a series of read-side optimizations in the appointments domain,
read performance for visit endpoints improved significantly compared
to the initial implementation.

The focus was on optimizing paginated visit reads under concurrent load.

No business logic or API contracts were changed.

## What Was Done

- Optimized visit read queries (UPCOMING / PAST)
- Added dedicated partial composite indexes for visit reads
- Reduced unnecessary query work and redundant sorting
- Introduced read-side caching for visit endpoints
- Tuned cache configuration for concurrent access
- Eliminated repeated authentication and redundant database calls during reads

## Results

### Before Optimization
- p95 latency: ~6 seconds
- average latency: ~4 seconds
- noticeable request failures under load
- poor scalability with 100 concurrent users

### After Optimization (warm cache)
- median latency: ~1.1–1.2 seconds
- average latency: ~1.1 seconds
- p95 latency: ~2.6–3.0 seconds
- 0% request failure rate
- stable behavior with 100 concurrent users
- significantly improved throughput (~45–46 requests/sec)

### Notes

- Remaining tail latency is primarily caused by response size,
  pagination, sorting, and JSON serialization under high concurrency
- Database performance is no longer the dominant bottleneck

---

## Online Consultation

Read-side optimizations were applied to the online consultations domain to
improve correctness, predictability, and reduce unnecessary database work
under concurrent load.

The focus was on optimizing single-entity read access for
`GET /online-consultation/{id}` in a system containing hundreds of thousands
of appointments.

No business logic, validation rules, or API contracts were changed.

## What Was Done

- Replaced entity-based loading with DTO projection for read operations
- Eliminated unnecessary entity graph traversal and lazy-loading overhead
- Introduced read-side caching for online consultation details
- Added explicit cache eviction on update and delete operations
- Removed cache synchronization (`sync = true`) to avoid contention
- Fixed endpoint path inconsistencies causing false-negative failures
- Ensured deterministic test data for reliable performance testing

## Results

### Before Optimization
- p95 latency: ~3.8–4.0 seconds
- average latency: ~1.1 seconds
- high tail latency under concurrent load
- unnecessary database work for simple read operations

### After Optimization
- median latency: ~15–35 ms
- average latency: ~900–1000 ms
- p95 latency: ~3.3–3.8 seconds
- 0% request failure rate
- stable throughput (~50 requests/sec with 100 concurrent users)
- functional correctness and predictable behavior under load

### Notes

- High p95 latency is caused by low cache hit ratio under high-cardinality access patterns
- Further improvements require query-level optimization and/or different caching strategy
  (e.g. short-lived hot-set caching or denormalized read models)

---

## Laboratory Test

Read-side optimizations were applied to the laboratory tests domain to
improve scalability and reduce database load under concurrent access.

The focus was on optimizing paginated patient test history retrieval via
`GET /test/history` for a system containing hundreds of thousands of appointments
and lab results.

No business logic, validation rules, or API contracts were changed.

## What Was Done

- Replaced entity-based loading with DTO-based read projections
- Removed entity graphs and deep object traversal from read path
- Split read model into:
    - lightweight test header query
    - bulk lab result lookup by test IDs
- Eliminated N+1 query patterns for lab results
- Introduced read-side caching for patient test history pages
- Added dedicated partial index for TEST appointments by patient and date
- Ensured deterministic test data for reliable performance measurements
- Verified execution plans using `EXPLAIN ANALYZE`

## Results

### Before Optimization
- p95 latency: ~6.0 seconds
- average latency: ~4.0 seconds
- high database load caused by JOIN-heavy queries
- excessive entity materialization and lazy-loading overhead
- poor scalability under concurrent access

### After Optimization (warm cache)
- median latency: ~750–880 ms
- average latency: ~1000 ms
- p95 latency: ~2.6–2.9 seconds
- 0% request failure rate
- stable throughput (~46–49 requests/sec with 100 concurrent users)
- database execution time ~300–350 ms per query (verified via EXPLAIN ANALYZE)

## Notes

- Remaining tail latency is dominated by:
    - large response payload size
    - JSON serialization cost
    - nested collection mapping under high concurrency
- Database performance is no longer the primary bottleneck
- Further p95 reduction would require API-level changes, such as:
    - splitting test history and lab results into separate endpoints
    - reducing response payload size
    - introducing alternative read models for large histories

## Conclusion

Under realistic mixed read workload, p95 latency stabilizes around ~3s with no functional errors.
Tail latency is dominated by heavy payload endpoints and high-cardinality access patterns.

---

# Lifecycle

## Results

### Before Optimization

- p95 latency: ~5.1 seconds
- average latency: ~3.2 seconds
- HTTP request failure rate: ~14.8%
- total requests executed: ~3,000
- throughput: ~23.5 requests/second

### After Optimization (warm cache)

- median latency: ~1.8–2.1 seconds
- average latency: ~1.5–1.6 seconds
- p95 latency: ~3.0–3.1 seconds
- HTTP request failure rate: ~0%
- throughput: ~37–40 requests/second
- stable functional behavior with 100 concurrent users

## Notes

- Read-side optimizations significantly reduced average latency and improved throughput
- p95 latency remains dominated by heavy read endpoints under mixed load
- Failure rate is primarily driven by non-read endpoints and expected 404 responses
  under high-cardinality access patterns
- Database performance is no longer the dominant bottleneck under lifecycle load
- Mixed read/write contention remains the primary scalability limiter

## Conclusion

Under realistic mixed lifecycle workload, average latency and throughput
improved substantially compared to the initial implementation.

However, p95 latency stabilizes around ~3 seconds, and request failure rate
remains unchanged due to inherent write contention and mixed traffic patterns.

Further improvements would require architectural changes beyond read-side
optimization, such as write isolation, asynchronous processing, or
separation of read and write models.
