CREATE EXTENSION IF NOT EXISTS pg_trgm;

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

CREATE TABLE IF NOT EXISTS clinics
(
    id          bigserial PRIMARY KEY,
    name        varchar(255) NOT NULL,
    logo_url    varchar(255),
    description text,
    address_id  bigint       NOT NULL,
    CONSTRAINT fk_clinics_addresses FOREIGN KEY (address_id) REFERENCES addresses (id)
);

CREATE TABLE IF NOT EXISTS doctors
(
    id      bigserial PRIMARY KEY,
    bio     varchar(255) NOT NULL DEFAULT '',
    user_id bigint       NOT NULL,
    CONSTRAINT uq_doctors_user UNIQUE (user_id)
);

CREATE TABLE IF NOT EXISTS doctor_details
(
    doctor_id      bigint PRIMARY KEY,
    phone_number   varchar(255) NOT NULL,
    license_number varchar(255) NOT NULL UNIQUE,
    CONSTRAINT fk_doctor_details_doctors FOREIGN KEY (doctor_id) REFERENCES doctors (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS doctor_clinics
(
    doctor_id bigint NOT NULL,
    clinic_id bigint NOT NULL,
    CONSTRAINT pk_doctor_clinics PRIMARY KEY (doctor_id, clinic_id),
    CONSTRAINT fk_dc_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id) ON DELETE CASCADE,
    CONSTRAINT fk_dc_clinic FOREIGN KEY (clinic_id) REFERENCES clinics (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS specializations
(
    id          bigserial PRIMARY KEY,
    name        varchar(50) NOT NULL,
    description text        NOT NULL
);

CREATE TABLE IF NOT EXISTS services
(
    id               bigserial PRIMARY KEY,
    name             varchar(255)   NOT NULL,
    description      text,
    price            decimal(10, 2) NOT NULL,
    duration_minutes int            NOT NULL,
    CONSTRAINT chk_price_positive CHECK (price >= 0),
    CONSTRAINT chk_duration_positive CHECK (duration_minutes > 0)
);

CREATE TABLE IF NOT EXISTS doctor_specializations
(
    doctor_id         bigint NOT NULL,
    specialization_id bigint NOT NULL,
    CONSTRAINT pk_doctor_specializations PRIMARY KEY (doctor_id, specialization_id),
    CONSTRAINT fk_doctor_specializations_doctors FOREIGN KEY (doctor_id) REFERENCES doctors (id) ON DELETE CASCADE,
    CONSTRAINT fk_doctor_specializations_specializations FOREIGN KEY (specialization_id) REFERENCES specializations (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS service_specializations
(
    specialization_id bigint NOT NULL,
    service_id        bigint NOT NULL,
    CONSTRAINT pk_service_specializations PRIMARY KEY (specialization_id, service_id),
    CONSTRAINT fk_service_specializations_specializations FOREIGN KEY (specialization_id) REFERENCES specializations (id),
    CONSTRAINT fk_service_specializations_services FOREIGN KEY (service_id) REFERENCES services (id)
);

CREATE INDEX IF NOT EXISTS idx_doctors_user ON doctors (user_id);
CREATE INDEX IF NOT EXISTS idx_specializations_name_gin ON specializations USING gin (name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_services_name_gin ON services USING gin (name gin_trgm_ops);
