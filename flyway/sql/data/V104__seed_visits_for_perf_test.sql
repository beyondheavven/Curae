INSERT INTO appointments (
    patient_id,
    scheduled_date,
    scheduled_time,
    status,
    type,
    created_at,
    updated_at
)
SELECT
    p.id,
    CURRENT_DATE - (gs % 180),
    TIME '08:00' + (gs % 10) * INTERVAL '30 minutes',
    'COMPLETED',
    'VISIT',
    NOW() - (gs % 365) * INTERVAL '1 day',
    NOW()
FROM patients p
    JOIN users u ON u.id = p.user_id
    JOIN generate_series(1, 8000) gs ON true
WHERE u.email IN (
    'perf1@mail.com',
    'perf2@mail.com',
    'perf3@mail.com',
    'perf4@mail.com',
    'perf5@mail.com',
    'perf6@mail.com'
    );

INSERT INTO visits (
    appointment_id,
    doctor_id,
    service_id,
    clinic_id
)
SELECT
    a.id,
    d.id,
    s.id,
    c.id
FROM appointments a
         JOIN patients p ON p.id = a.patient_id
         JOIN users u ON u.id = p.user_id
         JOIN doctors d ON d.id = (
    SELECT d2.id
    FROM doctors d2
             JOIN users u2 ON u2.id = d2.user_id
    WHERE u2.email = 'house@med.com'
)
         JOIN services s ON s.id = 1
         JOIN clinics c ON c.id = 1
WHERE u.email IN (
                  'perf1@mail.com',
                  'perf2@mail.com',
                  'perf3@mail.com',
                  'perf4@mail.com',
                  'perf5@mail.com',
                  'perf6@mail.com'
    )
  AND a.type = 'VISIT';
