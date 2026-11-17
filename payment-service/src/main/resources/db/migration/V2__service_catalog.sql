CREATE TABLE IF NOT EXISTS service_catalog
(
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(19, 2) NOT NULL
);

INSERT INTO service_catalog (id, name, description, price)
VALUES (1, 'General Checkup', 'Routine physical examination', 150.00),
       (2, 'Complex Diagnosis', 'In-depth analysis of symptoms', 500.00),
       (3, 'Quick Consultation', 'Brief medical advice', 75.00)
ON CONFLICT (id) DO NOTHING;
