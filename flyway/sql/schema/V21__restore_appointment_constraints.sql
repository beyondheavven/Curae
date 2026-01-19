-- Restore correct appointment constraints after appointment/referral refactor
-- Previous migration relaxed constraints incorrectly.
-- Appointments must always have a doctor.

-- 1. Safety check: ensure no NULL doctor_id exists
-- (If this UPDATE affects rows, it must be reviewed)
UPDATE appointments
SET doctor_id = 1
WHERE doctor_id IS NULL;

-- 2. Restore NOT NULL constraint for doctor_id
ALTER TABLE appointments
    ALTER COLUMN doctor_id SET NOT NULL;
