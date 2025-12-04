-- ==========================================
-- FOREIGN KEYS
-- ==========================================

-- Reference: appointment_doctor (table: appointments)
ALTER TABLE appointments ADD CONSTRAINT appointment_doctor
    FOREIGN KEY (doctor_id)
        REFERENCES doctors (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: appointment_patient (table: appointments)
ALTER TABLE appointments ADD CONSTRAINT appointment_patient
    FOREIGN KEY (patient_id)
        REFERENCES patients (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: appointments_services (table: appointments)
ALTER TABLE appointments ADD CONSTRAINT appointments_services
    FOREIGN KEY (service_id)
        REFERENCES services (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: clinics_addresses (table: clinics)
ALTER TABLE clinics ADD CONSTRAINT clinics_addresses
    FOREIGN KEY (address_id)
        REFERENCES addresses (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: doctor_clinic (table: doctors)
ALTER TABLE doctors ADD CONSTRAINT doctor_clinic
    FOREIGN KEY (clinic_id)
        REFERENCES clinics (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: doctor_schedules_doctors (table: doctor_availabilities)
ALTER TABLE doctor_availabilities ADD CONSTRAINT doctor_schedules_doctors
    FOREIGN KEY (doctor_id)
        REFERENCES doctors (id)
        ON DELETE CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: doctor_specializations_doctors (table: doctor_specializations)
-- FIXED: doctors_id -> doctor_id
ALTER TABLE doctor_specializations ADD CONSTRAINT doctor_specializations_doctors
    FOREIGN KEY (doctor_id)
        REFERENCES doctors (id)
        ON DELETE CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: doctor_specializations_specializations (table: doctor_specializations)
ALTER TABLE doctor_specializations ADD CONSTRAINT doctor_specializations_specializations
    FOREIGN KEY (specialization_id)
        REFERENCES specializations (id)
        ON DELETE CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: doctor_users (table: doctors)
ALTER TABLE doctors ADD CONSTRAINT doctor_users
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: online_consultation_appointments (table: online_consultation)
ALTER TABLE online_consultation ADD CONSTRAINT online_consultation_appointments
    FOREIGN KEY (appointment_id)
        REFERENCES appointments (id)
        ON DELETE  CASCADE
        ON UPDATE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: patient_user (table: patients)
ALTER TABLE patients ADD CONSTRAINT patient_user
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE  CASCADE
        ON UPDATE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: patients_addresses (table: patients)
ALTER TABLE patients ADD CONSTRAINT patients_addresses
    FOREIGN KEY (address_id)
        REFERENCES addresses (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: service_categories_specializations (table: service_categories)
ALTER TABLE service_categories ADD CONSTRAINT service_categories_specializations
    FOREIGN KEY (specialization_id)
        REFERENCES specializations (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: services_service_categories (table: services)
ALTER TABLE services ADD CONSTRAINT services_service_categories
    FOREIGN KEY (service_category_id)
        REFERENCES service_categories (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: tests_appointments (table: tests)
ALTER TABLE tests ADD CONSTRAINT tests_appointments
    FOREIGN KEY (appointment_id)
        REFERENCES appointments (id)
        ON DELETE  CASCADE
        ON UPDATE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: user_roles_roles (table: user_roles)
ALTER TABLE user_roles ADD CONSTRAINT user_roles_roles
    FOREIGN KEY (role_id)
        REFERENCES roles (id)
        ON DELETE CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: user_roles_users (table: user_roles)
ALTER TABLE user_roles ADD CONSTRAINT user_roles_users
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;

-- Reference: visits_appointments (table: visits)
ALTER TABLE visits ADD CONSTRAINT visits_appointments
    FOREIGN KEY (appointment_id)
        REFERENCES appointments (id)
        ON DELETE  CASCADE
        ON UPDATE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE;