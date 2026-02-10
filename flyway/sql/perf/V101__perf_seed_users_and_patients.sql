INSERT INTO addresses (
    street_name,
    city_name,
    house_number,
    state_name,
    apartment_number,
    zip_code,
    phone
)
VALUES
    ('Perf Street', 'Test City', '1', 'TS', '1A', '10001', '+1000000001'),
    ('Perf Street', 'Test City', '2', 'TS', '2A', '10002', '+1000000002'),
    ('Perf Street', 'Test City', '3', 'TS', '3A', '10003', '+1000000003'),
    ('Perf Street', 'Test City', '4', 'TS', '4A', '10004', '+1000000004'),
    ('Perf Street', 'Test City', '5', 'TS', '5A', '10005', '+1000000005'),
    ('Perf Street', 'Test City', '6', 'TS', '6A', '10006', '+1000000006');

INSERT INTO users (
    email,
    password_hash,
    first_name,
    last_name,
    role,
    status,
    date_of_birth
)
VALUES
    ('perf1@mail.com', '$2a$12$Wf4OuPkFrVa86PSw.ltuRelbcK/zPHdkA9DLJ7WZlN3e9y8aAzJCa', 'Perf', 'User1', 'PATIENT', 'ACTIVE', '1990-01-01'),
    ('perf2@mail.com', '$2a$12$Wf4OuPkFrVa86PSw.ltuRelbcK/zPHdkA9DLJ7WZlN3e9y8aAzJCa', 'Perf', 'User2', 'PATIENT', 'ACTIVE', '1992-01-01'),
    ('perf3@mail.com', '$2a$12$Wf4OuPkFrVa86PSw.ltuRelbcK/zPHdkA9DLJ7WZlN3e9y8aAzJCa', 'Perf', 'User3', 'PATIENT', 'ACTIVE', '1988-01-01'),
    ('perf4@mail.com', '$2a$12$Wf4OuPkFrVa86PSw.ltuRelbcK/zPHdkA9DLJ7WZlN3e9y8aAzJCa', 'Perf', 'User4', 'PATIENT', 'ACTIVE', '1991-01-01'),
    ('perf5@mail.com', '$2a$12$Wf4OuPkFrVa86PSw.ltuRelbcK/zPHdkA9DLJ7WZlN3e9y8aAzJCa', 'Perf', 'User5', 'PATIENT', 'ACTIVE', '1989-01-01'),
    ('perf6@mail.com', '$2a$12$Wf4OuPkFrVa86PSw.ltuRelbcK/zPHdkA9DLJ7WZlN3e9y8aAzJCa', 'Perf', 'User6', 'PATIENT', 'ACTIVE', '1993-01-01');

INSERT INTO patients (
    citizenship,
    gender,
    user_id,
    address_id
)
VALUES
    ('American', 'MALE',
     (SELECT id FROM users WHERE email = 'perf1@mail.com'),
     (SELECT id FROM addresses WHERE phone = '+1000000001')),
    ('American', 'FEMALE',
     (SELECT id FROM users WHERE email = 'perf2@mail.com'),
     (SELECT id FROM addresses WHERE phone = '+1000000002')),
    ('American', 'MALE',
     (SELECT id FROM users WHERE email = 'perf3@mail.com'),
     (SELECT id FROM addresses WHERE phone = '+1000000003')),
    ('American', 'FEMALE',
     (SELECT id FROM users WHERE email = 'perf4@mail.com'),
     (SELECT id FROM addresses WHERE phone = '+1000000004')),
    ('American', 'MALE',
     (SELECT id FROM users WHERE email = 'perf5@mail.com'),
     (SELECT id FROM addresses WHERE phone = '+1000000005')),
    ('American', 'FEMALE',
     (SELECT id FROM users WHERE email = 'perf6@mail.com'),
     (SELECT id FROM addresses WHERE phone = '+1000000006'));
