package com.vention.medfast.appointment.referral.repository;

import com.vention.medfast.appointment.referral.model.Referral;
import com.vention.medfast.appointment.referral.model.ReferralStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findByPatientIdAndStatusOrderByExpiresAtAsc(
        Long patientId,
        ReferralStatus status
    );
}
