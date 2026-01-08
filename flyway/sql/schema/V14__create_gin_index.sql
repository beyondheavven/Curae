CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_users_first_name_gin
    ON users USING gin (first_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_users_last_name_gin
    ON users USING gin (last_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_specializations_name_gin
    ON specializations USING gin (name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_services_name_gin
    ON services USING gin (name gin_trgm_ops);
