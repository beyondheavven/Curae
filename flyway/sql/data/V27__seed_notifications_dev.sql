INSERT INTO appointments (scheduled_date,
                          scheduled_time,
                          status,
                          doctor_id,
                          patient_id,
                          service_id,
                          created_at,
                          updated_at,
                          type)
VALUES (CURRENT_DATE + INTERVAL '5 days',
        TIME '10:00',
        'SCHEDULED',
        1,
        1,
        1,
        CURRENT_TIMESTAMP - INTERVAL '10 days',
        CURRENT_TIMESTAMP - INTERVAL '10 days',
        'VISIT');


update appointments
set status = 'CANCELLED'
where id = 6;

INSERT INTO notifications (patient_id,
                           appointment_id,
                           type,
                           status,
                           created_at)
VALUES (1,
        6,
        'APPOINTMENT_CANCELLED',
        'UNREAD',
        CURRENT_TIMESTAMP);

-- update test result ready notification
INSERT INTO appointments (scheduled_date, scheduled_time, status, patient_id, doctor_id, service_id, type)
VALUES (CURRENT_DATE - INTERVAL '10 days', '09:00', 'COMPLETED', 1, NULL, NULL, 'TEST');

INSERT INTO notifications (patient_id,
                           appointment_id,
                           type,
                           status,
                           created_at)
VALUES (1,
        7,
        'LAB_RESULT_READY',
        'UNREAD',
        CURRENT_TIMESTAMP - INTERVAL '5 days');
