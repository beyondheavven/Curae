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

update appointments
set type = 'TEST'
where id in (4,5);

ALTER TABLE appointments
ALTER COLUMN type SET NOT NULL;
