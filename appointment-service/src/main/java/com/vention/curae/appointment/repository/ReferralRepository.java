package com.vention.curae.appointment.repository;

import com.vention.curae.appointment.model.Referral;
import com.vention.curae.appointment.model.ReferralStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralRepository extends JpaRepository<Referral, Long> {

	List<Referral> findByPatientIdAndStatus(Long patientId, ReferralStatus status);

}
