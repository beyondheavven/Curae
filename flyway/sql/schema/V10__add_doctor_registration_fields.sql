ALTER TABLE users ADD COLUMN date_of_birth DATE;

UPDATE users u
SET    date_of_birth = p.date_of_birth
FROM   patients p
WHERE  u.id = p.user_id;

UPDATE users
SET    date_of_birth = '2000-01-01'
WHERE  date_of_birth IS NULL;

ALTER TABLE users ALTER COLUMN date_of_birth SET NOT NULL;

ALTER TABLE users ADD CONSTRAINT chk_user_dob_valid
    CHECK (date_of_birth > DATE '1915-01-01');

ALTER TABLE patients DROP COLUMN date_of_birth;

CREATE TABLE doctor_details(
    doctor_id      BIGSERIAL    PRIMARY KEY,
    phone_number   VARCHAR(255) NOT NULL,
    license_number VARCHAR(255) NOT NULL UNIQUE,

    CONSTRAINT fk_doctor_details_doctors
        FOREIGN KEY (doctor_id)
            REFERENCES doctors(id)
            ON DELETE CASCADE
);
