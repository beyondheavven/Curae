DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_status') THEN
        CREATE TYPE user_status AS ENUM ('ACTIVE', 'UNVERIFIED', 'BLOCKED');
    END IF;
END$$;

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE IF NOT EXISTS users
(
    id            bigserial PRIMARY KEY,
    email         varchar(255) NOT NULL UNIQUE,
    password_hash varchar(255) NOT NULL,
    first_name    varchar(255) NOT NULL,
    last_name     varchar(255) NOT NULL,
    role          varchar(20)  NOT NULL,
    profile_url   varchar(255),
    date_of_birth date         NOT NULL,
    status        user_status  NOT NULL,

    CONSTRAINT users_role_chk
        CHECK (role IN ('ADMIN', 'PATIENT', 'DOCTOR')),

    CONSTRAINT chk_user_dob_valid
        CHECK (date_of_birth > DATE '1915-01-01'),

    CONSTRAINT users_status_chk
        CHECK (status IN ('ACTIVE', 'UNVERIFIED', 'BLOCKED'))
);

CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id              BIGSERIAL PRIMARY KEY,
    users_id        BIGINT       NOT NULL,
    token           VARCHAR(512) NOT NULL UNIQUE,
    expiration_time timestamp    NOT NULL,

    CONSTRAINT refresh_tokens_user_fk
        FOREIGN KEY (users_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS email_verification_token
(
    id            BIGSERIAL PRIMARY KEY,
    users_id      BIGINT      NOT NULL,
    token         VARCHAR(64) NOT NULL,
    exp_date      TIMESTAMP   NOT NULL,
    create_date   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type          VARCHAR(25) NOT NULL,
    code          VARCHAR(255),
    status        VARCHAR(25) NOT NULL,
    resend_count  INT         NOT NULL DEFAULT 0,
    blocked_until TIMESTAMP,

    CONSTRAINT email_verification_token_user_fk
        FOREIGN KEY (users_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_email_verification_token_token
        UNIQUE (token)
);

CREATE INDEX IF NOT EXISTS idx_users_first_name_gin
    ON users USING gin (first_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_users_last_name_gin
    ON users USING gin (last_name gin_trgm_ops);

CREATE TABLE IF NOT EXISTS countries
(
    id          bigserial PRIMARY KEY,
    iso_code    varchar(2)   NOT NULL UNIQUE,
    name        varchar(255) NOT NULL,
    citizenship varchar(255) NOT NULL,
    zip_regex   varchar(255) NOT NULL,
    zip_example varchar(20)
);

INSERT INTO countries (iso_code, name, citizenship, zip_regex, zip_example)
VALUES ('US', 'United States', 'American', '^[0-9]{5}(-[0-9]{4})?$', '90210'),
       ('CA', 'Canada', 'Canadian', '^[A-Za-z][0-9][A-Za-z][ -]?[0-9][A-Za-z][0-9]$', 'K1A 0B1'),
       ('DE', 'Germany', 'German', '^[0-9]{5}$', '10115'),
       ('FR', 'France', 'French', '^[0-9]{5}$', '75001'),
       ('IT', 'Italy', 'Italian', '^[0-9]{5}$', '00100'),
       ('ES', 'Spain', 'Spanish', '^[0-9]{5}$', '28001'),
       ('NL', 'Netherlands', 'Dutch', '^[0-9]{4}\\s?[A-Za-z]{2}$', '1011 AC'),
       ('GB', 'United Kingdom', 'British', '^[A-Za-z]{1,2}[0-9][A-Za-z0-9]? ?[0-9][A-Za-z]{2}$', 'SW1A 1AA'),
       ('SE', 'Sweden', 'Swedish', '^[0-9]{3}\\s?[0-9]{2}$', '114 55'),
       ('NO', 'Norway', 'Norwegian', '^[0-9]{4}$', '0150'),
       ('UA', 'Ukraine', 'Ukrainian', '^[0-9]{5}$', '01001'),
       ('BY', 'Belarus', 'Belarusian', '^[0-9]{6}$', '220030')
ON CONFLICT (iso_code) DO NOTHING;
