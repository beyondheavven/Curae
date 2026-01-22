CREATE TABLE purchases (
    id BIGSERIAL PRIMARY KEY,

    patient_id BIGINT NOT NULL,

    service_id BIGINT NOT NULL,

    stripe_payment_intent_id VARCHAR(255) NOT NULL UNIQUE,

    amount DECIMAL(19, 2) NOT NULL,

    currency VARCHAR(3) NOT NULL DEFAULT 'usd',

    status VARCHAR(50) NOT NULL CHECK (status IN ('SUCCESS', 'FAILED', 'REFUNDED')),

    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),

    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),

    CONSTRAINT fk_purchases_patient FOREIGN KEY (patient_id) REFERENCES patients (id),

    CONSTRAINT fk_purchases_service FOREIGN KEY (service_id) REFERENCES services (id)
);

CREATE INDEX idx_purchases_patient ON purchases (patient_id);

CREATE INDEX idx_purchases_service ON purchases (service_id);
