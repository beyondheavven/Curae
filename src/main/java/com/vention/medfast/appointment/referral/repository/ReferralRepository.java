package com.vention.medfast.appointment.referral.repository;

import com.vention.medfast.appointment.referral.model.Referral;
import com.vention.medfast.appointment.referral.model.ReferralStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {

	@Query("""
			    select r
			    from Referral r
			    where r.patient.id = :patientId
			      and r.status = com.vention.medfast.appointment.referral.model.ReferralStatus.ACTIVE
			      and r.expiresAt >= :today
			    order by r.expiresAt asc
			""")
	List<Referral> findActiveByPatient(@Param("patientId") Long patientId, @Param("today") LocalDate today);

}
