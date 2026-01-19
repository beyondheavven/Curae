ALTER TABLE appointments
ADD COLUMN type VARCHAR(20);

UPDATE appointments
SET type = 'VISIT'
WHERE type IS NULL;

ALTER TABLE appointments
ALTER COLUMN type SET NOT NULL;
