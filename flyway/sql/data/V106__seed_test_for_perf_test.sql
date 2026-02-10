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
    TIME '08:00' + (gs % 6) * INTERVAL '1 hour',
    'COMPLETED',
    'TEST',
    NOW() - (gs % 90) * INTERVAL '1 day',
    NOW()
FROM patients p
    JOIN users u ON u.id = p.user_id
    JOIN generate_series(1, 20) gs ON true
WHERE u.email IN (
    'perf1@mail.com',
    'perf2@mail.com',
    'perf3@mail.com',
    'perf4@mail.com',
    'perf5@mail.com',
    'perf6@mail.com'
    );

INSERT INTO tests (
    appointment_id,
    clinic_id
)
SELECT
    a.id,
    (SELECT id FROM clinics LIMIT 1)
FROM appointments a
    JOIN patients p ON p.id = a.patient_id
    JOIN users u ON u.id = p.user_id
WHERE a.type = 'TEST'
  AND u.email IN (
    'perf1@mail.com',
    'perf2@mail.com',
    'perf3@mail.com',
    'perf4@mail.com',
    'perf5@mail.com',
    'perf6@mail.com'
    )
ON CONFLICT DO NOTHING;

INSERT INTO lab_results (
    test_id,
    lab_test_id,
    status,
    result_url,
    uploaded_at
)
SELECT
    t.appointment_id,
    lt.id,
    'READY',
    'https://files.med.com/results/' || t.appointment_id || '_' || lt.id || '.pdf',
    NOW() - INTERVAL '2 days'
FROM tests t
    JOIN appointments a ON a.id = t.appointment_id
    JOIN patients p ON p.id = a.patient_id
    JOIN users u ON u.id = p.user_id
    JOIN lab_tests lt ON lt.id IN (
    SELECT id FROM lab_tests ORDER BY id LIMIT 3
    )
WHERE u.email IN (
    'perf1@mail.com',
    'perf2@mail.com',
    'perf3@mail.com',
    'perf4@mail.com',
    'perf5@mail.com',
    'perf6@mail.com'
    )
ON CONFLICT DO NOTHING;
