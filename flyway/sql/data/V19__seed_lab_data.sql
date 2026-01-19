INSERT INTO lab_tests (name, description, price)
VALUES
    ('Complete Blood Count (CBC)', 'Basic screening test', 35.00),
    ('Vitamin D (25-OH)', 'Check for vitamin D deficiency', 120.00),
    ('Glucose Test', 'Fast for 8 hours', 20.00),
    ('TSH', 'Thyroid function test', 45.00),
    ('Ferritin', 'Iron levels', 55.00)
    ON CONFLICT DO NOTHING;

INSERT INTO appointments ( scheduled_date, scheduled_time, status, patient_id, doctor_id, service_id)
VALUES ( CURRENT_DATE - INTERVAL '10 days', '09:00', 'COMPLETED', 2, NULL, NULL);
--
INSERT INTO lab_results (appointment_id, lab_test_id, status, result_url, uploaded_at)
VALUES
    (4, 1, 'READY', 'link', CURRENT_TIMESTAMP - INTERVAL '9 days'),
    (4, 2, 'READY', 'link', CURRENT_TIMESTAMP - INTERVAL '9 days'),
    (4, 3, 'READY', 'link', CURRENT_TIMESTAMP - INTERVAL '9 days'),
    (4, 4, 'PROCESSING', NULL, NULL);


INSERT INTO appointments ( scheduled_date, scheduled_time, status, patient_id, doctor_id, service_id)
VALUES ( CURRENT_DATE + INTERVAL '3 days', '11:11', 'SCHEDULED', 2, NULL, NULL);

INSERT INTO lab_results (appointment_id, lab_test_id, status, result_url, uploaded_at)
VALUES (5, 5, 'PROCESSING', NULL, NULL),
       (5,1, 'PROCESSING', NULL, NULL);



