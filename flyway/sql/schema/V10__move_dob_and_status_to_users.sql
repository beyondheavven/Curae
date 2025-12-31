-- Moving date_of_birth from 'patients' to 'users'
ALTER TABLE users ADD COLUMN date_of_birth DATE;

UPDATE users u
SET    date_of_birth = p.date_of_birth
FROM   patients p
WHERE  u.id = p.user_id;

UPDATE users
SET    date_of_birth = '2000-01-01'
WHERE  date_of_birth IS NULL;

ALTER TABLE users ADD CONSTRAINT chk_user_dob_valid
    CHECK (date_of_birth > DATE '1915-01-01');

ALTER TABLE users ALTER COLUMN date_of_birth SET NOT NULL;

ALTER TABLE patients DROP COLUMN IF EXISTS date_of_birth;

-- Moving status from 'patients' to 'users'
ALTER TYPE patient_status RENAME TO user_status;

ALTER TABLE users ADD COLUMN status user_status;

UPDATE users
SET    status = 'ACTIVE'::user_status
WHERE  role = 'ADMIN';

UPDATE users
SET    status = patients.status::text::user_status
FROM   patients
WHERE  users.id = patients.user_id;

UPDATE users
SET    status = 'UNVERIFIED'::user_status
WHERE  status IS NULL;

ALTER TABLE users ALTER COLUMN status SET NOT NULL;

ALTER TABLE users ADD CONSTRAINT users_status_chk
    CHECK (status IN ('ACTIVE', 'UNVERIFIED', 'BLOCKED'));

ALTER TABLE patients DROP COLUMN IF EXISTS status;
