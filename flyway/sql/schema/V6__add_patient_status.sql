DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'patient_status') THEN
CREATE TYPE patient_status AS ENUM ('ACTIVE', 'UNVERIFIED', 'BLOCKED');
END IF;
END$$;

