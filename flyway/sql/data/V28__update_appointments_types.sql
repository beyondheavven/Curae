UPDATE appointments
SET type = 'TEST'
WHERE id IN (
    SELECT DISTINCT appointment_id
    FROM lab_results
);
