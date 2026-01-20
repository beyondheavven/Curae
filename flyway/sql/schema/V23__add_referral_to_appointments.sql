ALTER TABLE appointments
ADD COLUMN referral_id BIGINT;

ALTER TABLE appointments
ADD CONSTRAINT fk_appointments_referral
FOREIGN KEY (referral_id) REFERENCES referrals(id);
