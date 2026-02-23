DROP INDEX IF EXISTS idx_appt_visit_past;
DROP INDEX IF EXISTS idx_appt_visit_upcoming;
DROP INDEX IF EXISTS idx_tests_patient_date;

ALTER TABLE appointments DROP CONSTRAINT IF EXISTS chk_appointments_type;
ALTER TABLE appointments DROP CONSTRAINT IF EXISTS chk_appointments_status;

ALTER TABLE appointments
ALTER COLUMN type TYPE VARCHAR(20)
    USING type::text;

ALTER TABLE appointments
ALTER COLUMN status TYPE VARCHAR(30)
    USING status::text;

DROP TYPE IF EXISTS appointment_type_enum;
DROP TYPE IF EXISTS appointment_status_enum;

CREATE INDEX idx_appt_visit_past
    ON appointments (patient_id, scheduled_date DESC, scheduled_time DESC)
    WHERE type = 'VISIT';

CREATE INDEX idx_appt_visit_upcoming
    ON appointments (patient_id, scheduled_date, scheduled_time)
    WHERE type = 'VISIT';

CREATE INDEX idx_tests_patient_date
    ON appointments (patient_id, scheduled_date DESC)
    WHERE type = 'TEST';

ALTER TABLE appointments
    ADD CONSTRAINT chk_appointments_type
        CHECK (type IN ('VISIT','TEST','ONLINE'));

ALTER TABLE appointments
    ADD CONSTRAINT chk_appointments_status
        CHECK (status IN (
                          'SCHEDULED',
                          'SCHEDULED_CONFIRMED',
                          'CANCELLED',
                          'IN_CONSULTATION',
                          'COMPLETED',
                          'MISSED'
            ));
