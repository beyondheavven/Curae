CREATE TABLE doctor_details(
    doctor_id      BIGSERIAL    PRIMARY KEY,
    phone_number   VARCHAR(255) NOT NULL,
    license_number VARCHAR(255) NOT NULL UNIQUE,

    CONSTRAINT fk_doctor_details_doctors
        FOREIGN KEY (doctor_id)
            REFERENCES doctors(id)
            ON DELETE CASCADE
);
