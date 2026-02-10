CREATE TYPE referral_status AS ENUM (
    'ACTIVE',
    'EXPIRED',
    'USED'
);

CREATE TABLE referrals (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    issued_by BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    appointment_type VARCHAR(20) NOT NULL,
    expires_at DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT fk_referral_patient
        FOREIGN KEY (patient_id)
            REFERENCES patients(id),

    CONSTRAINT fk_referral_doctor
        FOREIGN KEY (issued_by)
            REFERENCES doctors(id),

    CONSTRAINT fk_referral_service
        FOREIGN KEY (service_id)
            REFERENCES services(id)
);
