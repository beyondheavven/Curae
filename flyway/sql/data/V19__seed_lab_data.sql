INSERT INTO lab_tests (name, description, price)
VALUES
('Complete Blood Count (CBC)', 'Basic screening test to examine overall health', 35.00),
('Vitamin D (25-OH)', 'Check for vitamin D deficiency. Blood draw required.', 120.00),
('Glucose Test', 'Fast for 8 hours before the test.', 20.00),
('TSH (Thyroid Stimulating Hormone)', 'Test to evaluate thyroid function.', 45.00),
('Ferritin', 'Measures the amount of iron in your blood.', 55.00);

INSERT INTO appointments (scheduled_date, scheduled_time, status, patient_id, doctor_id, service_id)
VALUES
(CURRENT_DATE - INTERVAL '2 days', '08:00', 'COMPLETED', 1, NULL, NULL),
(CURRENT_DATE, '09:30', 'COMPLETED', 1, NULL, NULL),
(CURRENT_DATE + INTERVAL '3 days', '10:00', 'SCHEDULED', 2, NULL, NULL);

INSERT INTO lab_results (appointment_id, lab_test_id, status, result_url, uploaded_at)
VALUES
(4, 1, 'READY', 'link1', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(4, 3, 'READY', 'link2', CURRENT_TIMESTAMP - INTERVAL '1 day'),

(5, 2, 'PROCESSING', NULL, NULL),
(6, 5, 'PROCESSING', NULL, NULL);
