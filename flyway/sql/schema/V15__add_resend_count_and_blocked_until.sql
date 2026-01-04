ALTER TABLE email_verification_token
    ADD COLUMN resend_count INT NOT NULL DEFAULT 0;

ALTER TABLE email_verification_token
    ADD COLUMN blocked_until TIMESTAMP;
