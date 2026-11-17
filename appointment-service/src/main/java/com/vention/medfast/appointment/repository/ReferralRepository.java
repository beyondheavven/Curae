package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.model.Referral;
import com.vention.medfast.appointment.model.ReferralStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralRepository extends JpaRepository<Referral, Long> {

	List<Referral> findByPatientIdAndStatus(Long patientId, ReferralStatus status);

}
