CREATE TABLE IF NOT EXISTS chats (
    id         BIGSERIAL PRIMARY KEY,
    patient_id BIGINT      NOT NULL,
    doctor_id  BIGINT      NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_chat_participants UNIQUE (patient_id, doctor_id)
);

CREATE TABLE IF NOT EXISTS chat_messages (
    id         BIGSERIAL PRIMARY KEY,
    chat_id    BIGINT        NOT NULL REFERENCES chats (id) ON DELETE CASCADE,
    sender_id  BIGINT        NOT NULL,
    content    VARCHAR(2000) NOT NULL,
    created_at TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_message_chat_id ON chat_messages (chat_id);
