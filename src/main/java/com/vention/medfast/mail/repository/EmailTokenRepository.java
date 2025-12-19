package com.vention.medfast.mail.repository;

import com.vention.medfast.mail.model.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {

	Optional<EmailToken> findByTokenHash(String tokenHash);

}
