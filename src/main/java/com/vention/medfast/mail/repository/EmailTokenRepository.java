package com.vention.medfast.mail.repository;

import com.vention.medfast.mail.model.EmailToken;
import com.vention.medfast.mail.model.EmailTokenStatus;
import com.vention.medfast.mail.model.EmailTokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    Optional<EmailToken> findByTokenHash(String tokenHash);
    Optional<EmailToken> findTopByUserEmailAndTypeAndStatusOrderByCreatedAtDesc(String email, EmailTokenType type, EmailTokenStatus status);
}
