CREATE TYPE lab_result_status AS ENUM ('PROCESSING', 'READY', 'FAILED');

CREATE TABLE lab_results (
                             id             BIGSERIAL PRIMARY KEY,
                             appointment_id BIGINT NOT NULL,
                             lab_test_id    BIGINT NOT NULL,

                             status         lab_result_status NOT NULL DEFAULT 'PROCESSING',

                             result_url     VARCHAR(255),
                             uploaded_at    TIMESTAMP,

                             CONSTRAINT fk_lab_results_appointment
                                 FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,

                             CONSTRAINT fk_lab_results_catalog
                                 FOREIGN KEY (lab_test_id) REFERENCES lab_tests(id)
);

CREATE INDEX idx_lab_results_appointment ON lab_results(appointment_id);
