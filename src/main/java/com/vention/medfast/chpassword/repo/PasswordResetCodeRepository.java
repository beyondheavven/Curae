package com.vention.medfast.chpassword.repo;

import com.vention.medfast.chpassword.model.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {
    Optional<PasswordResetCode> findTopByUserEmailAndUsedIsFalseOrderByExpiresAtDesc(String email);
}
