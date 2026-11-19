package com.vention.curae.email.repository;

import com.vention.curae.email.model.EmailTokenType;
import com.vention.curae.email.model.EmailVerificationToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

	Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

	Optional<EmailVerificationToken> findTopByUserEmailAndTypeOrderByCreatedAtDesc(String email, EmailTokenType type);

}
