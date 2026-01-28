--Appointments
ALTER TABLE appointments DROP COLUMN IF EXISTS doctor_id CASCADE;
ALTER TABLE appointments DROP COLUMN IF EXISTS service_id CASCADE;


--Online Consultations
ALTER TABLE IF EXISTS online_consultation RENAME TO online_consultations;
ALTER TABLE online_consultations ADD COLUMN IF NOT EXISTS doctor_id BIGINT;

ALTER TABLE online_consultations ADD CONSTRAINT fk_online_consultations_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors (id);

--Visits
ALTER TABLE visits ADD COLUMN IF NOT EXISTS doctor_id BIGINT;
ALTER TABLE visits ADD COLUMN IF NOT EXISTS service_id BIGINT;

UPDATE visits v
SET doctor_id = a.doctor_id,
    service_id = a.service_id
    FROM appointments a
WHERE v.appointment_id = a.id;

ALTER TABLE visits ADD CONSTRAINT fk_visits_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors (id);

ALTER TABLE visits ADD CONSTRAINT fk_visits_service
        FOREIGN KEY (service_id) REFERENCES services (id);
--Tests
