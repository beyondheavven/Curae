INSERT INTO appointments (
    scheduled_date,
    scheduled_time,
    status,
    patient_id,
    type,
    created_at,
    updated_at
)
SELECT
            CURRENT_DATE - (random() * 365)::int,
        time '08:00' + (random() * interval '8 hours'),
            (ARRAY['SCHEDULED', 'SCHEDULED_CONFIRMED', 'COMPLETED', 'CANCELLED', 'MISSED'])
    [floor(random() * 5 + 1)],
    (SELECT id FROM patients ORDER BY random() LIMIT 1),
    (ARRAY[
        'VISIT'::appointment_type_enum,
        'TEST'::appointment_type_enum,
        'ONLINE'::appointment_type_enum
    ])[floor(random() * 3 + 1)],
    NOW() - (random() * interval '365 days'),
    NOW()
FROM generate_series(1, 500000);
