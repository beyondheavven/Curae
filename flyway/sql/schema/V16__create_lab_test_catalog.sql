CREATE TABLE lab_tests (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price       DECIMAL(10, 2) NOT NULL DEFAULT 0,


    CONSTRAINT chk_lab_test_price_positive CHECK (price >= 0)
);

CREATE INDEX idx_lab_tests_name_gin ON lab_tests USING gin (name gin_trgm_ops);
