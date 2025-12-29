CREATE TABLE doctor_clinics(
    doctor_id BIGINT NOT NULL,
    clinic_id BIGINT NOT NULL,

    CONSTRAINT pk_doctor_clinics PRIMARY KEY (doctor_id, clinic_id),

    CONSTRAINT fk_dc_doctor
        FOREIGN KEY (doctor_id)
            REFERENCES doctors(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_dc_clinic
        FOREIGN KEY (clinic_id)
            REFERENCES clinics(id)
            ON DELETE CASCADE
);

INSERT INTO doctor_clinics (doctor_id, clinic_id)
SELECT id, clinic_id
FROM  doctors
WHERE clinic_id IS NOT NULL;

DROP INDEX IF EXISTS idx_doctors_clinic;

ALTER TABLE doctors DROP CONSTRAINT fk_doctor_clinic;

ALTER TABLE doctors DROP COLUMN clinic_id;
