package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.model.OnlineConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnlineConsultationRepository extends JpaRepository<OnlineConsultation, Long> {

	@Query("""
			    select new com.vention.medfast.appointment.dto.OnlineConsultationDetails(
			        a.id,
			        p.id,
			        d.id,
			        a.date,
			        a.time,
			        a.status,
			        a.type,
			        a.updatedAt,
			        oc.meetingLink
			    )
			    from OnlineConsultation oc
			    join Appointment a on a.id = oc.id
			    join oc.patient p
			    join oc.doctor d
			    where oc.id = :id
			""")
	Optional<OnlineConsultationDetails> findDetailsById(@Param("id") Long id);

}
