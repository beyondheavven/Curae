package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.dto.VisitListItemDto;
import com.vention.medfast.appointment.model.Visit;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

	@Query("""
			    select new com.vention.medfast.appointment.dto.VisitListItemDto(
			        du.firstName,
			        du.lastName,
			        c.name,
			        addr.city,
			        a.date,
			        a.time,
			        a.status
			    )
			    from Appointment a
			    join Visit v on v.id = a.id
			    join v.doctor d
			    join d.user du
			    join v.clinic c
			    join c.address addr
			    where a.patient.id = :patientId
			      and a.type = com.vention.medfast.appointment.model.AppointmentType.VISIT
			      and (a.date, a.time) > (:today, :now)
			    order by a.date, a.time
			""")
	Slice<VisitListItemDto> findUpcomingByPatient(@Param("patientId") Long patientId, @Param("today") LocalDate today,
			@Param("now") LocalTime now, Pageable pageable);

	@Query("""
			    select new com.vention.medfast.appointment.dto.VisitListItemDto(
			        du.firstName,
			        du.lastName,
			        c.name,
			        addr.city,
			        a.date,
			        a.time,
			        a.status
			    )
			    from Appointment a
			    join Visit v on v.id = a.id
			    join v.doctor d
			    join d.user du
			    join v.clinic c
			    join c.address addr
			    where a.patient.id = :patientId
			      and a.type = com.vention.medfast.appointment.model.AppointmentType.VISIT
			      and (a.date, a.time) <= (:today, :now)
			    order by a.date desc, a.time desc
			""")
	Slice<VisitListItemDto> findPastByPatient(@Param("patientId") Long patientId, @Param("today") LocalDate today,
			@Param("now") LocalTime now, Pageable pageable);

}
