CREATE TABLE password_reset_codes (
                                      id BIGSERIAL PRIMARY KEY,
                                      user_id BIGINT NOT NULL,
                                      code VARCHAR(10) NOT NULL,
                                      expires_at TIMESTAMP NOT NULL,
                                      used BOOLEAN NOT NULL DEFAULT FALSE,

                                      CONSTRAINT fk_reset_user
                                          FOREIGN KEY (user_id)
                                              REFERENCES users(id)
                                              ON DELETE CASCADE
);
