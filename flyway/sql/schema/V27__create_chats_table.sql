CREATE TABLE chats (
    id BIGSERIAL PRIMARY KEY,

    patient_id BIGINT NOT NULL,
    doctor_id  BIGINT NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),

    CONSTRAINT uk_chat_participants
        UNIQUE (patient_id, doctor_id),

    CONSTRAINT chk_chat_not_same_user
        CHECK (patient_id <> doctor_id)
);

CREATE INDEX idx_chats_patient_id ON chats (patient_id);
CREATE INDEX idx_chats_doctor_id ON chats (doctor_id);
