DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'appointment_type_enum') THEN
        CREATE TYPE appointment_type_enum AS ENUM ('VISIT', 'TEST', 'ONLINE');
    END IF;
END$$;

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE IF NOT EXISTS referrals
(
    id               BIGSERIAL PRIMARY KEY,
    patient_id       BIGINT      NOT NULL,
    issued_by        BIGINT      NOT NULL,
    service_id       BIGINT      NOT NULL,
    appointment_type VARCHAR(20) NOT NULL,
    expires_at       DATE        NOT NULL,
    status           VARCHAR(20) NOT NULL,
    created_at       TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS doctor_availabilities
(
    id                bigserial PRIMARY KEY,
    start_time        time   NOT NULL,
    end_time          time   NOT NULL,
    time_step_minutes int    NOT NULL,
    availability_date date   NOT NULL,
    doctor_id         bigint NOT NULL,

    CONSTRAINT chk_end_after_start
        CHECK (end_time > start_time),

    CONSTRAINT chk_step_positive
        CHECK (time_step_minutes > 0)
);

CREATE TABLE IF NOT EXISTS appointments
(
    id             bigserial PRIMARY KEY,
    scheduled_date date        NOT NULL,
    scheduled_time time        NOT NULL,
    status         varchar(30) NOT NULL,
    patient_id     bigint      NOT NULL,
    created_at     timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type           appointment_type_enum NOT NULL,
    referral_id    bigint,

    CONSTRAINT appointments_status_chk
        CHECK (status IN (
                          'SCHEDULED',
                          'SCHEDULED_CONFIRMED',
                          'CANCELLED',
                          'IN_CONSULTATION',
                          'COMPLETED',
                          'MISSED'
            )),

    CONSTRAINT chk_updated_after_created
        CHECK (updated_at >= created_at),

    CONSTRAINT fk_appointments_referral
        FOREIGN KEY (referral_id)
            REFERENCES referrals (id)
);

CREATE TABLE IF NOT EXISTS online_consultations
(
    appointment_id bigint NOT NULL PRIMARY KEY,
    doctor_id      BIGINT,
    meeting_link   varchar(255),

    CONSTRAINT fk_online_consultation_appointments
        FOREIGN KEY (appointment_id)
            REFERENCES appointments (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS tests
(
    appointment_id bigint NOT NULL PRIMARY KEY,
    clinic_id      BIGINT,

    CONSTRAINT fk_tests_appointments
        FOREIGN KEY (appointment_id)
            REFERENCES appointments (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS visits
(
    appointment_id bigint NOT NULL PRIMARY KEY,
    diagnosis      text,
    symptoms       text,
    medical_report text,
    clinic_id      BIGINT NOT NULL,
    doctor_id      BIGINT,
    service_id     BIGINT,

    CONSTRAINT fk_visits_appointments
        FOREIGN KEY (appointment_id)
            REFERENCES appointments (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS lab_tests
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price       DECIMAL(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT chk_lab_test_price_positive CHECK (price >= 0)
);

CREATE TABLE IF NOT EXISTS lab_results
(
    id          BIGSERIAL PRIMARY KEY,
    test_id     BIGINT      NOT NULL,
    lab_test_id BIGINT      NOT NULL,
    status      VARCHAR(30) NOT NULL DEFAULT 'PROCESSING',
    result_url  VARCHAR(255),
    uploaded_at TIMESTAMP,

    CONSTRAINT fk_lab_results_test
        FOREIGN KEY (test_id)
            REFERENCES tests (appointment_id),

    CONSTRAINT fk_lab_results_catalog
        FOREIGN KEY (lab_test_id)
            REFERENCES lab_tests (id)
);

CREATE INDEX IF NOT EXISTS idx_appointments_patient
    ON appointments (patient_id);

CREATE INDEX IF NOT EXISTS idx_appointments_date
    ON appointments (scheduled_date);

CREATE INDEX IF NOT EXISTS idx_appt_visit_upcoming
    ON appointments (patient_id, scheduled_date, scheduled_time)
    WHERE type = 'VISIT';

CREATE INDEX IF NOT EXISTS idx_appt_visit_past
    ON appointments (patient_id, scheduled_date DESC, scheduled_time DESC)
    WHERE type = 'VISIT';

CREATE INDEX IF NOT EXISTS idx_tests_patient_date
    ON appointments (patient_id, scheduled_date DESC)
    WHERE type = 'TEST';

CREATE INDEX IF NOT EXISTS idx_lab_results_appointment
    ON lab_results (test_id);

CREATE INDEX IF NOT EXISTS idx_lab_tests_name_gin
    ON lab_tests USING gin (name gin_trgm_ops);
