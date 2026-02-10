INSERT INTO referrals (
    patient_id,
    issued_by,
    service_id,
    appointment_type,
    expires_at,
    status
)
SELECT
    p.id                              AS patient_id,
    d.id                              AS issued_by,
    s.id                              AS service_id,
    'VISIT'                           AS appointment_type,
    CURRENT_DATE + INTERVAL '30 days' AS expires_at,
    'ACTIVE'                          AS status
FROM patients p
    JOIN users u ON u.id = p.user_id
    JOIN doctors d ON d.id = (
    SELECT d2.id
    FROM doctors d2
    JOIN users u2 ON u2.id = d2.user_id
    WHERE u2.email = 'house@med.com'
    )
    JOIN services s ON s.id = 1
WHERE u.email IN (
    'perf1@mail.com',
    'perf2@mail.com',
    'perf3@mail.com',
    'perf4@mail.com',
    'perf5@mail.com',
    'perf6@mail.com'
    )
ON CONFLICT DO NOTHING;
