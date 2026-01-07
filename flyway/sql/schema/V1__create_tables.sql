-- Last modification date: 2025-12-04
-- All ENUM types replaced with VARCHAR + CHECK constraints

-- ==========================================
-- TABLES
-- ==========================================

-- Table: users
CREATE TABLE users
(
    id            bigserial PRIMARY KEY,
    email         varchar(255) NOT NULL UNIQUE,
    password_hash varchar(255) NOT NULL,
    first_name    varchar(255) NOT NULL,
    last_name     varchar(255) NOT NULL,

    role          varchar(20)  NOT NULL,
    profile_url   varchar(255),

    CONSTRAINT users_role_chk
        CHECK (role IN ('ADMIN', 'PATIENT', 'DOCTOR'))
);

-- Table: refresh_tokens
CREATE TABLE refresh_tokens
(
    id         BIGSERIAL PRIMARY KEY,
    users_id   BIGINT       NOT NULL,
    token      VARCHAR(512) NOT NULL UNIQUE,

    status     varchar(15)  NOT NULL,
    created_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT refresh_tokens_status_chk
        CHECK (status IN ('VALID', 'EXPIRED', 'REVOKED')),

    CONSTRAINT refresh_tokens_user_fk
        FOREIGN KEY (users_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

-- Table: addresses
CREATE TABLE addresses
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

-- Table: clinics
CREATE TABLE clinics
(
    id          bigserial PRIMARY KEY,
    name        varchar(255) NOT NULL,
    logo_url    varchar(255),
    description text,
    address_id  bigint       NOT NULL
);

-- Table: doctors
CREATE TABLE doctors
(
    id        bigserial PRIMARY KEY,
    bio       varchar(255) NOT NULL,
    user_id   bigint       NOT NULL,
    clinic_id bigint       NOT NULL
);

-- Table: patients
CREATE TABLE patients
(
    id            bigserial PRIMARY KEY,
    citizenship   varchar(255) NOT NULL,

    gender        varchar(10)  NOT NULL,
    date_of_birth date         NOT NULL,
    user_id       bigint       NOT NULL,
    address_id    bigint       NOT NULL,

    status        varchar(15)  NOT NULL,

    CONSTRAINT patients_gender_chk
        CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),

    CONSTRAINT patients_status_chk
        CHECK (status IN ('ACTIVE', 'UNVERIFIED', 'BLOCKED')),

    CONSTRAINT chk_dob_valid
        CHECK (date_of_birth > DATE '1915-01-01')
);

-- Table: services
CREATE TABLE services
(
    id               bigserial PRIMARY KEY,
    name             varchar(255)   NOT NULL,
    description      text,
    price            decimal(10, 2) NOT NULL,
    duration_minutes int            NOT NULL,

    CONSTRAINT chk_price_positive
        CHECK (price >= 0),

    CONSTRAINT chk_duration_positive
        CHECK (duration_minutes > 0)
);

-- Table: specializations
CREATE TABLE specializations
(
    id          bigserial PRIMARY KEY,
    name        varchar(50) NOT NULL,
    description text        NOT NULL
);

-- Table: doctor_specializations
CREATE TABLE doctor_specializations
(
    doctor_id         bigint NOT NULL,
    specialization_id bigint NOT NULL,
    CONSTRAINT pk_doctor_specializations PRIMARY KEY (doctor_id, specialization_id)
);

-- Table: service_specializations
CREATE TABLE service_specializations
(
    specialization_id bigint NOT NULL,
    service_id        bigint NOT NULL,
    CONSTRAINT pk_service_specializations PRIMARY KEY (specialization_id, service_id)
);

-- Table: doctor_availabilities
CREATE TABLE doctor_availabilities
(
    id                bigserial PRIMARY KEY,
    start_time        time   NOT NULL,
    end_time          time   NOT NULL,
    time_step_minutes int    NOT NULL,
    availability_date date   NOT NULL,
    doctor_id         bigint NOT NULL,

    CONSTRAINT chk_end_after_start
        CHECK (end_time > start_time),

    CONSTRAINT chk_step_positive
        CHECK (time_step_minutes > 0)
);

-- Table: appointments
CREATE TABLE appointments
(
    id             bigserial PRIMARY KEY,
    scheduled_date date        NOT NULL,
    scheduled_time time        NOT NULL,

    status         varchar(30) NOT NULL,
    doctor_id      bigint      NOT NULL,
    patient_id     bigint      NOT NULL,
    service_id     bigint      NOT NULL,

    created_at     timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT appointments_status_chk
        CHECK (status IN (
                          'SCHEDULED',
                          'SCHEDULED_CONFIRMED',
                          'CANCELLED',
                          'IN_CONSULTATION',
                          'COMPLETED',
                          'MISSED'
            )),

    CONSTRAINT chk_updated_after_created
        CHECK (updated_at >= created_at)
);

-- Table: online_consultation
CREATE TABLE online_consultation
(
    appointment_id bigint NOT NULL PRIMARY KEY,
    meeting_link   varchar(255)
);

-- Table: tests
CREATE TABLE tests
(
    appointment_id bigint NOT NULL PRIMARY KEY,
    result_url     varchar(255),
    uploaded_at    timestamp
);

-- Table: visits
CREATE TABLE visits
(
    appointment_id bigint NOT NULL PRIMARY KEY,
    diagnosis      text,
    symptoms       text,
    medical_report text
);
