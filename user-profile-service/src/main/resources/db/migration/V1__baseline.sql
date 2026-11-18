CREATE TABLE IF NOT EXISTS addresses
(
    id               bigserial PRIMARY KEY,
    street_name      varchar(255) NOT NULL,
    city_name        varchar(255) NOT NULL,
    house_number     varchar(255) NOT NULL,
    state_name       varchar(255) NOT NULL,
    apartment_number varchar(255) NOT NULL,
    zip_code         varchar(255) NOT NULL,
    phone            varchar(255)
);

CREATE TABLE IF NOT EXISTS patients
(
    id          bigserial PRIMARY KEY,
    citizenship varchar(255) NOT NULL,
    gender      varchar(10)  NOT NULL,
    user_id     bigint       NOT NULL,
    address_id  bigint       NOT NULL,

    CONSTRAINT patients_gender_chk CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT fk_patients_addresses FOREIGN KEY (address_id) REFERENCES addresses (id) ON DELETE RESTRICT,
    CONSTRAINT uq_patients_address UNIQUE (address_id),
    CONSTRAINT uq_patients_user UNIQUE (user_id)
);

CREATE INDEX IF NOT EXISTS idx_patients_user ON patients (user_id);

CREATE TABLE IF NOT EXISTS user_snapshots
(
    user_id     bigint PRIMARY KEY,
    email       varchar(255) NOT NULL,
    first_name  varchar(255) NOT NULL,
    last_name   varchar(255) NOT NULL,
    birth_date  date,
    profile_url varchar(255),
    status      varchar(30)  NOT NULL
);
