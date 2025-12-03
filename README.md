# MedFast PostgreSQL Docker Setup

## Description

This project provides a fully reproducible PostgreSQL environment with Flyway-based schema versioning.
Database initialization and migrations are automated using Docker Compose.

## Project Structure

```
.
├─ docker-compose.yml
├─ .env
├─ flyway/
│  └─ sql/
│     └─ V1__init.sql
└─ README.md
```

## Quick Start

### Start the Environment

```bash
docker compose up -d
```

* Starts the PostgreSQL container with persistent storage
* Automatically runs Flyway migrations

### Stop the Environment

```bash
docker compose down -v
```

* Removes containers, networks, and volumes

## Flyway Migrations

* Migration scripts are located in `flyway/sql/`
* Naming convention: `V<version>__<description>.sql`
* To run migrations manually:

```bash
docker compose run --rm flyway migrate
```

## Initialization Scripts

* Located in `docker-entrypoint-initdb.d/` (Postgres automatically executes them on first startup)
* Create schemas, tables, and seed essential data

## Configuration

* Configured via `.env` file:

```
POSTGRES_USER=medfast_user
POSTGRES_PASSWORD=medfast_password
POSTGRES_DB=medfast_db
POSTGRES_PORT=5432
```

## Notes

* Simply running `docker compose up` is sufficient; no manual steps are required.
