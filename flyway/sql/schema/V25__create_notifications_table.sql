CREATE TABLE notifications
(
    id             BIGSERIAL PRIMARY KEY,
    patient_id     BIGINT       NOT NULL,
    appointment_id BIGINT       NULL,
    type           VARCHAR(50)  NOT NULL,
    status        varchar(10)      NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notifications_patient
        FOREIGN KEY (patient_id)
            REFERENCES patients (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_notifications_appointment
        FOREIGN KEY (appointment_id)
            REFERENCES appointments (id)
            ON DELETE SET NULL
);
