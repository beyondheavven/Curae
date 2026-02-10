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
WHERE u.email IN (
    'perf1@mail.com',
    'perf2@mail.com',
    'perf3@mail.com',
    'perf4@mail.com',
    'perf5@mail.com',
    'perf6@mail.com'
    )
  AND a.type = 'TEST'
    LIMIT 20
ON CONFLICT DO NOTHING;

INSERT INTO lab_results (
    test_id,
    lab_test_id,
    status,
    result_url,
    uploaded_at
)
SELECT
    t.id,
    lt.id,
    'READY',
    'https://files.med.com/results/' || t.id || '_' || lt.id || '.pdf',
    NOW() - INTERVAL '2 days'
FROM tests t
    JOIN appointments a ON a.id = t.appointment_id
    JOIN patients p ON p.id = a.patient_id
    JOIN users u ON u.id = p.user_id
    JOIN lab_tests lt ON lt.id IN (1, 2, 3)
WHERE u.email IN (
    'perf1@mail.com',
    'perf2@mail.com',
    'perf3@mail.com',
    'perf4@mail.com',
    'perf5@mail.com',
    'perf6@mail.com'
    )
ON CONFLICT DO NOTHING;
