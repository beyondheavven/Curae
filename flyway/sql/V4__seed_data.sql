-- 1. ADDRESSES
INSERT INTO addresses (id, street_name, city_name, house_number, state_name, apartment_number, zip_code, phone)
VALUES
    (1, 'Broadway', 'New York', '100', 'NY', '101', '10001', '+12125550100'),
    (2, 'Maple Avenue', 'Boston', '25', 'MA', '4B', '02108', '+16175550200'),
    (3, 'Ocean Drive', 'Miami', '500', 'FL', '12', '33101', '+13055550300');

-- 2. USERS
INSERT INTO users (id, email, password_hash, first_name, last_name, role, profile_url)
VALUES
-- Admin
(1, 'admin@med.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4hZ.h5.W5a', 'Admin', 'User', 'ADMIN', NULL),
-- Doctors
(2, 'house@med.com', '$2a$12$Ex9wW1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4hZ.h5.X9b', 'Gregory', 'House', 'DOCTOR', 'https://img.url/house.jpg'),
(3, 'watson@med.com', '$2a$12$Gz2yX1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4hZ.h5.Y3c', 'John', 'Watson', 'DOCTOR', NULL),
-- Patients
(4, 'jane.doe@mail.com', '$2a$12$Jk4zY1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4hZ.h5.Z8d', 'Jane', 'Doe', 'PATIENT', NULL),
(5, 'john.smith@mail.com', '$2a$12$Nm7vV1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4hZ.h5.A2e', 'John', 'Smith', 'PATIENT', NULL);

-- 3. SPECIALIZATIONS
INSERT INTO specializations (id, name, description)
VALUES
    (1, 'General Practitioner', 'Primary care provider dealing with a wide range of diseases'),
    (2, 'Cardiologist', 'Heart and cardiovascular system specialist'),
    (3, 'Diagnostician', 'Specialist in identifying complex medical conditions');

-- 4. CLINICS
INSERT INTO clinics (id, name, logo_url, description, address_id)
VALUES
    (1, 'Central City Hospital', 'logo.png', 'Advanced medical care center', 1);

-- 5. DOCTORS
INSERT INTO doctors (id, bio, user_id, clinic_id)
VALUES
    (1, 'World-renowned diagnostician with unconventional methods.', 2, 1),
    (2, 'Experienced general practitioner and surgeon.', 3, 1);

-- 6. DOCTOR SPECIALIZATIONS
INSERT INTO doctor_specializations (doctor_id, specialization_id)
VALUES
    (1, 3), -- House -> Diagnostician
    (1, 1), -- House -> General Practitioner
    (2, 1); -- Watson -> General Practitioner

-- 7. PATIENTS
INSERT INTO patients (id, citizenship, gender, date_of_birth, user_id, address_id)
VALUES
    (1, 'USA', 'FEMALE', '1990-05-15', 4, 2),
    (2, 'USA', 'MALE', '1985-10-20', 5, 3);

-- 8. SERVICE CATEGORIES
INSERT INTO service_categories (id, name, specialization_id)
VALUES
    (1, 'Consultations', 1),
    (2, 'Diagnostics', 3);

-- 9. SERVICES
INSERT INTO services (id, name, description, price, duration_minutes, service_category_id)
VALUES
    (1, 'General Checkup', 'Routine physical examination', 150.00, 30, 1),
    (2, 'Complex Diagnosis', 'In-depth analysis of symptoms', 500.00, 60, 2),
    (3, 'Quick Consultation', 'Brief medical advice', 75.00, 15, 1);

-- 10. DOCTOR AVAILABILITIES
INSERT INTO doctor_availabilities (id, start_time, end_time, time_step_minutes, availability_date, doctor_id)
VALUES
    (1, '09:00', '17:00', 30, CURRENT_DATE + INTERVAL '1 day', 1),
    (2, '10:00', '18:00', 30, CURRENT_DATE + INTERVAL '2 day', 2);

-- 11. APPOINTMENTS
INSERT INTO appointments (id, scheduled_date, status, doctor_id, patient_id, service_id, created_at, updated_at)
VALUES
    (1, CURRENT_DATE - INTERVAL '5 days', 'COMPLETED', 1, 1, 2, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
    (2, CURRENT_DATE + INTERVAL '1 day', 'SCHEDULED', 2, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, CURRENT_DATE + INTERVAL '2 days', 'SCHEDULED_CONFIRMED', 1, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 12. APPOINTMENT DETAILS

-- Visits
INSERT INTO visits (appointment_id, diagnosis, symptoms, cabinet_number, medical_report)
VALUES
    (1, 'Lupus', 'Severe leg pain, migraine, high fever', '304', 'Patient treated with steroids. Condition stable.');

-- Online Consultation
INSERT INTO online_consultation (appointment_id, meeting_link)
VALUES
    (3, 'https://zoom.us/j/987654321');

-- Tests
INSERT INTO tests (appointment_id, result_url, uploaded_at)
VALUES
    (1, 'https://files.med.com/results/bloodwork.pdf', CURRENT_TIMESTAMP - INTERVAL '4 days');
