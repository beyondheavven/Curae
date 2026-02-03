INSERT INTO lab_results (
    appointment_id,
    lab_test_id,
    status,
    result_url,
    uploaded_at
)
VALUES
    (4, 1, 'READY', 'https://files.med.com/results/bloodwork.pdf', CURRENT_TIMESTAMP - INTERVAL '9 days'),
    (4, 2, 'READY', 'https://files.med.com/results/vitamin_d.pdf', CURRENT_TIMESTAMP - INTERVAL '9 days'),
    (4, 3, 'READY', 'https://files.med.com/results/general.pdf', CURRENT_TIMESTAMP - INTERVAL '9 days'),
    (4, 4, 'PROCESSING', NULL, NULL),
    (5, 1, 'PROCESSING', NULL, NULL),
    (5, 5, 'PROCESSING', NULL, NULL);
