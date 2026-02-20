CREATE TABLE email_verification_token
(
    id          BIGSERIAL PRIMARY KEY,

    users_id    BIGINT      NOT NULL,
    token       VARCHAR(64) NOT NULL,

    exp_date    TIMESTAMP   NOT NULL,
    create_date TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    type        VARCHAR(25) NOT NULL,
    code        VARCHAR(255),
    status      VARCHAR(25) NOT NULL,

    CONSTRAINT email_verification_token_user_fk
        FOREIGN KEY (users_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_email_verification_token_token
        UNIQUE (token)
);
