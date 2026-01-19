CREATE TYPE appointment_type_enum AS ENUM (
    'VISIT',
    'TEST',
    'ONLINE'
);

ALTER TABLE appointments
    ADD COLUMN type appointment_type_enum;

UPDATE appointments
SET type = 'VISIT'
WHERE type IS NULL;

ALTER TABLE appointments
ALTER COLUMN type SET NOT NULL;
