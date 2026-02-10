CREATE INDEX idx_appt_visit_upcoming
    ON appointments (patient_id, scheduled_date, scheduled_time)
    WHERE type = 'VISIT';

CREATE INDEX idx_appt_visit_past
    ON appointments (patient_id, scheduled_date DESC, scheduled_time DESC)
    WHERE type = 'VISIT';

CREATE INDEX idx_tests_patient_date
    ON appointments (patient_id, scheduled_date DESC)
    WHERE type = 'TEST';
