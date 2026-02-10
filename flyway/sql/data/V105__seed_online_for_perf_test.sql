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
    CURRENT_DATE - (gs % 30),
    TIME '09:00' + (gs % 5) * INTERVAL '1 hour',
    'COMPLETED',
    'ONLINE',
    NOW() - (gs % 60) * INTERVAL '1 day',
    NOW()
FROM patients p
    JOIN users u ON u.id = p.user_id
    JOIN generate_series(1, 10) gs ON true
WHERE u.email IN (
    'perf1@mail.com',
    'perf2@mail.com',
    'perf3@mail.com',
    'perf4@mail.com',
    'perf5@mail.com',
    'perf6@mail.com'
    );

INSERT INTO online_consultations (
    appointment_id,
    doctor_id,
    meeting_link
)
SELECT
    a.id,
    d.id,
    'https://meet.med.com/' || a.id
FROM appointments a
         JOIN patients p ON p.id = a.patient_id
         JOIN users u ON u.id = p.user_id
         JOIN doctors d ON d.id = (
    SELECT d2.id
    FROM doctors d2
             JOIN users u2 ON u2.id = d2.user_id
    WHERE u2.email = 'house@med.com'
)
WHERE a.type = 'ONLINE'
    ON CONFLICT DO NOTHING;
