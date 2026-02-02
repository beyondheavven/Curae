--Online Consultations
ALTER TABLE IF EXISTS online_consultation RENAME TO online_consultations;
ALTER TABLE online_consultations ADD COLUMN IF NOT EXISTS doctor_id BIGINT;

--Visits
ALTER TABLE visits ADD COLUMN IF NOT EXISTS doctor_id BIGINT;
ALTER TABLE visits ADD COLUMN IF NOT EXISTS service_id BIGINT;

--Tests
ALTER TABLE tests ADD COLUMN IF NOT EXISTS clinic_id BIGINT;

--Lab results
ALTER TABLE lab_results RENAME COLUMN appointment_id TO test_id;

--Data
UPDATE online_consultations oc
SET doctor_id = a.doctor_id
    FROM appointments a
WHERE oc.appointment_id = a.id;

UPDATE visits v
SET doctor_id = a.doctor_id,
    service_id = a.service_id
    FROM appointments a
WHERE v.appointment_id = a.id;

UPDATE tests SET clinic_id = 1 WHERE clinic_id IS NULL;

--Drop data
DELETE FROM lab_results;
DELETE FROM tests;

--Constraints
--Online Consultations FK
ALTER TABLE online_consultations ADD CONSTRAINT fk_online_consultations_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors (id);

--Visits FK
ALTER TABLE visits ADD CONSTRAINT fk_visits_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors (id);

ALTER TABLE visits ADD CONSTRAINT fk_visits_service
        FOREIGN KEY (service_id) REFERENCES services (id);

--Tests FK
ALTER TABLE tests ADD CONSTRAINT fk_tests_clinic
        FOREIGN KEY (clinic_id) REFERENCES clinics (id);

--Lab Results
ALTER TABLE lab_results DROP CONSTRAINT IF EXISTS fk_lab_results_appointment;
ALTER TABLE lab_results ADD CONSTRAINT fk_lab_results_test
        FOREIGN KEY (test_id) REFERENCES tests (appointment_id);

--Drop columns
ALTER TABLE appointments DROP COLUMN IF EXISTS doctor_id CASCADE;
ALTER TABLE appointments DROP COLUMN IF EXISTS service_id CASCADE;

ALTER TABLE tests DROP COLUMN IF EXISTS result_url CASCADE;
ALTER TABLE tests DROP COLUMN IF EXISTS uploaded_at CASCADE;
