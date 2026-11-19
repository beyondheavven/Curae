CREATE TABLE IF NOT EXISTS doctor_feedbacks (
    id         BIGSERIAL PRIMARY KEY,
    rating     INT          NOT NULL,
    summary    VARCHAR(500) NOT NULL,
    doctor_id  BIGINT       NOT NULL,
    patient_id BIGINT       NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_doctor_feedback_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    CONSTRAINT uk_doctor_feedback_patient UNIQUE (patient_id, doctor_id)
);

CREATE TABLE IF NOT EXISTS clinic_feedbacks (
    id         BIGSERIAL PRIMARY KEY,
    rating     INT          NOT NULL,
    summary    VARCHAR(500) NOT NULL,
    clinic_id  BIGINT       NOT NULL,
    patient_id BIGINT       NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_clinic_feedback_clinic FOREIGN KEY (clinic_id) REFERENCES clinics (id),
    CONSTRAINT uk_clinic_feedback_patient UNIQUE (patient_id, clinic_id)
);