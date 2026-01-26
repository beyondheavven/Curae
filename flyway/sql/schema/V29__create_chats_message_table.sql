CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,

    chat_id   BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,

    content   VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),

    CONSTRAINT fk_message_chat
        FOREIGN KEY (chat_id)
        REFERENCES chats (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_chat_messages_chat_id_created_at
    ON chat_messages (chat_id, created_at DESC);
