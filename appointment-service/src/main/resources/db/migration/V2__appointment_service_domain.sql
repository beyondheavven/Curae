DROP INDEX IF EXISTS idx_appt_visit_upcoming;
DROP INDEX IF EXISTS idx_appt_visit_past;
DROP INDEX IF EXISTS idx_tests_patient_date;

ALTER TABLE appointments
    ALTER COLUMN type TYPE VARCHAR(20) USING type::text;

CREATE INDEX IF NOT EXISTS idx_appt_visit_upcoming
    ON appointments (patient_id, scheduled_date, scheduled_time)
    WHERE type = 'VISIT';

CREATE INDEX IF NOT EXISTS idx_appt_visit_past
    ON appointments (patient_id, scheduled_date DESC, scheduled_time DESC)
    WHERE type = 'VISIT';

CREATE INDEX IF NOT EXISTS idx_tests_patient_date
    ON appointments (patient_id, scheduled_date DESC)
    WHERE type = 'TEST';

ALTER TABLE visits
    ADD COLUMN IF NOT EXISTS doctor_first_name VARCHAR(100),
    ADD COLUMN IF NOT EXISTS doctor_last_name  VARCHAR(100),
    ADD COLUMN IF NOT EXISTS clinic_name       VARCHAR(255),
    ADD COLUMN IF NOT EXISTS city              VARCHAR(100);

INSERT INTO lab_tests (name, description, price)
SELECT 'General Checkup', 'Standard laboratory checkup', 50.00
WHERE NOT EXISTS (SELECT 1 FROM lab_tests WHERE name = 'General Checkup');

INSERT INTO lab_tests (name, description, price)
SELECT 'Blood Test', 'Complete blood count', 30.00
WHERE NOT EXISTS (SELECT 1 FROM lab_tests WHERE name = 'Blood Test');

INSERT INTO lab_tests (name, description, price)
SELECT 'Biochemistry', 'Extended biochemistry panel', 80.00
WHERE NOT EXISTS (SELECT 1 FROM lab_tests WHERE name = 'Biochemistry');
