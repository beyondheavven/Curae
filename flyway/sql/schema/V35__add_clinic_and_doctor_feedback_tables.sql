CREATE TABLE clinic_feedbacks
(
    id         bigserial PRIMARY KEY,
    patient_id bigint    NOT NULL,
    clinic_id  bigint    NOT NULL,
    rating     int       NOT NULL,
    summary    varchar(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_doctor_feedback_clinic
        FOREIGN KEY (clinic_id) REFERENCES clinics (id) ON DELETE CASCADE,

    CONSTRAINT fk_doctor_feedback_patient
        FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,

    CONSTRAINT uq_clinic_feedback UNIQUE (patient_id, clinic_id)
);


CREATE TABLE doctor_feedbacks
(
    id         bigserial PRIMARY KEY,
    patient_id bigint    NOT NULL,
    doctor_id  bigint    NOT NULL,
    rating     int       NOT NULL CHECK (rating BETWEEN 1 AND 5),
    summary    varchar(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_doctor_feedback_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors (id) ON DELETE CASCADE,

    CONSTRAINT fk_doctor_feedback_patient
        FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,

    CONSTRAINT uq_doctor_feedback UNIQUE (patient_id, doctor_id)
);

CREATE INDEX idx_doctor_feedback_doctor ON doctor_feedbacks (doctor_id);
CREATE INDEX idx_clinic_feedback_clinic ON clinic_feedbacks (clinic_id);
