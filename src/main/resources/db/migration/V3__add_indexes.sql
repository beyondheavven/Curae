-- ==========================================
-- INDEXES (Performance)
-- ==========================================

CREATE INDEX idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_service ON appointments(service_id);
CREATE INDEX idx_appointments_date ON appointments(scheduled_date);

CREATE INDEX idx_doctors_user ON doctors(user_id);
CREATE INDEX idx_doctors_clinic ON doctors(clinic_id);

CREATE INDEX idx_patients_user ON patients(user_id);

CREATE INDEX idx_services_category ON services(service_category_id);
CREATE INDEX idx_service_categories_spec ON service_categories(specialization_id);