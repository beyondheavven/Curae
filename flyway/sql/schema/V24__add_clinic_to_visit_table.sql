ALTER TABLE visits
    ADD COLUMN clinic_id BIGINT;

UPDATE visits
SET clinic_id = 1
WHERE clinic_id IS NULL;


ALTER TABLE visits
    ADD CONSTRAINT fk_visit_clinic
        FOREIGN KEY (clinic_id) REFERENCES clinics(id);


ALTER TABLE visits
    ALTER COLUMN clinic_id SET NOT NULL;
