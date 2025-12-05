-- Last modification date: 2025-12-04

-- ==========================================
-- ENUMS & TYPES
-- ==========================================

CREATE TYPE appointment_status_type AS ENUM (
    'SCHEDULED',
    'SCHEDULED_CONFIRMED',
    'CANCELLED',
    'IN_CONSULTATION',
    'COMPLETED',
    'MISSED'
);

CREATE TYPE gender_type AS ENUM (
	'MALE',
	'FEMALE',
	'NOT_SPECIFIED'
);

-- Table: users
ALTER TABLE users
    ADD COLUMN first_name varchar(255) NOT NULL DEFAULT 'Unknown',
    ADD COLUMN last_name varchar(255) NOT NULL DEFAULT 'Unknown',
    ADD COLUMN profile_url varchar(255);

ALTER TABLE users RENAME COLUMN username TO email;

ALTER TABLE users RENAME COLUMN password TO password_hash;

ALTER TABLE users
    ALTER COLUMN email TYPE varchar(255),
    ALTER COLUMN id TYPE bigint;

ALTER TABLE roles
    ALTER COLUMN id TYPE bigint;

ALTER TABLE user_roles
    ALTER COLUMN user_id TYPE bigint,
    ALTER COLUMN role_id TYPE bigint;

-- Table: addresses
CREATE TABLE addresses (
                           id bigserial,
                           street_name varchar(255)  NOT NULL,
                           city_name varchar(255)  NOT NULL,
                           house_number varchar(255)  NOT NULL,
                           state_name varchar(255)  NOT NULL,
                           apartment_number varchar(255)  NOT NULL,
                           zip_code varchar(255)  NOT NULL,
                           phone varchar(255),
                           CONSTRAINT pk_addresses PRIMARY KEY (id)
);

-- Table: appointments
CREATE TABLE appointments (
                              id bigserial,
                              scheduled_date date  NOT NULL,
                              status appointment_status_type  NOT NULL,
                              doctor_id bigint  NOT NULL,
                              patient_id bigint  NOT NULL,
                              service_id bigint  NOT NULL,
                              created_at timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT pk_appointments PRIMARY KEY (id),
                              CONSTRAINT chk_updated_after_created CHECK (updated_at >= created_at)
);

-- Table: clinics
CREATE TABLE clinics (
                         id bigserial,
                         name varchar(255)  NOT NULL,
                         logo_url varchar(255),
                         description text,
                         address_id bigint  NOT NULL,
                         CONSTRAINT pk_clinics PRIMARY KEY (id)
);

-- Table: doctor_availabilities
CREATE TABLE doctor_availabilities (
                                       id bigserial,
                                       start_time time  NOT NULL,
                                       end_time time  NOT NULL,
                                       time_step_minutes int  NOT NULL,
                                       availability_date date  NOT NULL,
                                       doctor_id bigint  NOT NULL,
                                       CONSTRAINT pk_doctor_availabilities PRIMARY KEY (id),
                                       CONSTRAINT chk_end_after_start CHECK (end_time > start_time),
                                       CONSTRAINT chk_step_positive CHECK (time_step_minutes > 0)
);

-- Table: doctor_specializations
CREATE TABLE doctor_specializations (
                                        doctor_id bigint  NOT NULL,
                                        specialization_id bigint  NOT NULL,
                                        CONSTRAINT pk_doctor_specializations PRIMARY KEY (doctor_id, specialization_id)
);

-- Table: doctors
CREATE TABLE doctors (
                         id bigserial,
                         bio varchar(255)  NOT NULL,
                         user_id bigint  NOT NULL,
                         clinic_id bigint  NOT NULL,
                         CONSTRAINT pk_doctors PRIMARY KEY (id)
);

-- Table: online_consultation
CREATE TABLE online_consultation (
                                     appointment_id bigint  NOT NULL,
                                     meeting_link varchar(255),
                                     CONSTRAINT pk_online_consultation PRIMARY KEY (appointment_id)
);

-- Table: patients
CREATE TABLE patients (
                          id bigserial,
                          citizenship varchar(255)  NOT NULL,
                          gender gender_type  NOT NULL,
                          date_of_birth date  NOT NULL,
                          user_id bigint  NOT NULL,
                          address_id bigint  NOT NULL,
                          CONSTRAINT pk_patients PRIMARY KEY (id),
                          CONSTRAINT chk_dob_valid CHECK (date_of_birth > '1915-01-01')
);

-- Table: service_categories
CREATE TABLE service_categories (
                                    id bigserial,
                                    name varchar(255)  NOT NULL,
                                    specialization_id bigint  NOT NULL,
                                    CONSTRAINT pk_service_categories PRIMARY KEY (id)
);

-- Table: services
CREATE TABLE services (
                          id bigserial,
                          name varchar(255)  NOT NULL,
                          description text,
                          price decimal(10,2)  NOT NULL,
                          duration_minutes int  NOT NULL,
                          service_category_id bigint  NOT NULL,
                          CONSTRAINT pk_services PRIMARY KEY (id),
                          CONSTRAINT chk_price_positive CHECK (price >= 0),
                          CONSTRAINT chk_duration_positive CHECK (duration_minutes > 0)
);

-- Table: specializations
CREATE TABLE specializations (
                                 id bigserial,
                                 name varchar(50)  NOT NULL,
                                 description text  NOT NULL,
                                 CONSTRAINT pk_specializations PRIMARY KEY (id)
);

-- Table: tests
CREATE TABLE tests (
                       appointment_id bigint  NOT NULL,
                       result_url varchar(255),
                       uploaded_at timestamp,
                       CONSTRAINT pk_tests PRIMARY KEY (appointment_id)
);


-- Table: visits
CREATE TABLE visits (
                        appointment_id bigint  NOT NULL,
                        diagnosis text,
                        symptoms text,
                        cabinet_number varchar(20)  NOT NULL,
                        medical_report text,
                        CONSTRAINT pk_visits PRIMARY KEY (appointment_id)
);