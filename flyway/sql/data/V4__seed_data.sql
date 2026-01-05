-- 1. ADDRESSES
INSERT INTO addresses (street_name, city_name, house_number, state_name, apartment_number, zip_code, phone)
VALUES ('Broadway', 'New York', '100', 'NY', '101', '10001', '+12125550100'),
       ('Maple Avenue', 'Boston', '25', 'MA', '4B', '02108', '+16175550200'),
       ('Ocean Drive', 'Miami', '500', 'FL', '12', '33101', '+13055550300');

-- 2. USERS
INSERT INTO users (email, password_hash, first_name, last_name, role, profile_url)
VALUES
-- Admin
('admin@med.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4hZ.h5.W5a', 'Admin', 'User', 'ADMIN', NULL),
-- Doctors
('house@med.com', '$2a$12$Ex9wW1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4hZ.h5.X9b', 'Gregory', 'House', 'DOCTOR',
 'https://img.url/house.jpg'),
('watson@med.com', '$2a$12$Gz2yX1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4hZ.h5.Y3c', 'John', 'Watson', 'DOCTOR', NULL),
-- Patients
('jane.doe@mail.com', '$2a$12$Jk4zY1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4hZ.h5.Z8d', 'Jane', 'Doe', 'PATIENT',
 NULL),
('john.smith@mail.com', '$2a$12$Nm7vV1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4hZ.h5.A2e', 'John', 'Smith', 'PATIENT',
 NULL),
('Test@test.com', '$2a$12$Wf4OuPkFrVa86PSw.ltuRelbcK/zPHdkA9DLJ7WZlN3e9y8aAzJCa', 'Test', 'Test', 'ADMIN', NULL);
-- id 6 pass test123

-- 3. SPECIALIZATIONS
INSERT INTO specializations (name, description)
VALUES ('General Practitioner', 'Primary care provider dealing with a wide range of diseases'),
       ('Cardiologist', 'Heart and cardiovascular system specialist'),
       ('Diagnostician', 'Specialist in identifying complex medical conditions');

-- 4. CLINICS
INSERT INTO clinics (name, logo_url, description, address_id)
VALUES ('Central City Hospital', 'logo.png', 'Advanced medical care center', 1);

-- 5. DOCTORS
INSERT INTO doctors (bio, user_id, clinic_id)
VALUES ('World-renowned diagnostician with unconventional methods.', 2, 1),
       ('Experienced general practitioner and surgeon.', 3, 1);

-- 6. DOCTOR SPECIALIZATIONS
INSERT INTO doctor_specializations (doctor_id, specialization_id)
VALUES (1, 3), -- House -> Diagnostician
       (1, 1), -- House -> General Practitioner
       (2, 1);
-- Watson -> General Practitioner

-- 7. PATIENTS
INSERT INTO patients (citizenship, gender, date_of_birth, user_id, address_id, status)
VALUES ('American', 'FEMALE', '1990-05-15', 4, 2, 'ACTIVE'),
       ('American', 'MALE', '1985-10-20', 5, 3, 'ACTIVE');

-- 8. SERVICES
INSERT INTO services (name, description, price, duration_minutes)
VALUES ('General Checkup', 'Routine physical examination', 150.00, 30),
       ('Complex Diagnosis', 'In-depth analysis of symptoms', 500.00, 60),
       ('Quick Consultation', 'Brief medical advice', 75.00, 15);

-- 9. SERVICE CATEGORIES
INSERT INTO service_specializations (specialization_id, service_id)
VALUES (1, 1),
       (1, 3),
       (2, 2),
       (3, 2);

-- 10. DOCTOR AVAILABILITIES
INSERT INTO doctor_availabilities (start_time, end_time, time_step_minutes, availability_date, doctor_id)
VALUES ('09:00', '17:00', 30, CURRENT_DATE + INTERVAL '1 day', 1),
       ('10:00', '18:00', 30, CURRENT_DATE + INTERVAL '2 day', 2);

-- 11. APPOINTMENTS
INSERT INTO appointments (scheduled_date, scheduled_time, status, doctor_id, patient_id, service_id, created_at,
                          updated_at)
VALUES (CURRENT_DATE - INTERVAL '5 days', TIME '10:00', 'COMPLETED', 1, 1, 2, CURRENT_TIMESTAMP - INTERVAL '10 days',
        CURRENT_TIMESTAMP - INTERVAL '5 days'),
       (CURRENT_DATE + INTERVAL '1 day', TIME '12:00', 'SCHEDULED', 2, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (CURRENT_DATE + INTERVAL '2 days', TIME '10:00', 'SCHEDULED_CONFIRMED', 1, 2, 2, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

-- 12. APPOINTMENT DETAILS

-- Visits
INSERT INTO visits (appointment_id, diagnosis, symptoms, medical_report)
VALUES (1, 'Lupus', 'Severe leg pain, migraine, high fever', 'Patient treated with steroids. Condition stable.');

-- Online Consultation
INSERT INTO online_consultation (appointment_id, meeting_link)
VALUES (3, 'https://zoom.us/j/987654321');

-- Tests
INSERT INTO tests (appointment_id, result_url, uploaded_at)
VALUES (1, 'https://files.med.com/results/bloodwork.pdf', CURRENT_TIMESTAMP - INTERVAL '4 days');


