CREATE TABLE IF NOT EXISTS notifications
(
    id             BIGSERIAL PRIMARY KEY,
    patient_id     BIGINT       NOT NULL,
    appointment_id BIGINT       NULL,
    type           VARCHAR(50)  NOT NULL,
    status         varchar(10)  NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
