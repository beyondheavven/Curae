ALTER TABLE users
ALTER COLUMN role TYPE VARCHAR(50)
    USING role::text;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_type WHERE typname = 'role_type') THEN
DROP TYPE role_type;
END IF;
END $$;
