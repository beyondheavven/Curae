package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.model.Visit;
import org.springframework.data.domain.Page;
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
			    select v from Visit v
			    where v.patient.id = :patientId
			      and (
			          v.date > :today
			          or (v.date = :today and v.time > :now)
			      )
			    order by v.date, v.time
			""")
	Page<Visit> findUpcomingByPatient(@Param("patientId") Long patientId, @Param("today") LocalDate today,
			@Param("now") LocalTime now, Pageable pageable);

	@Query("""
			    select v from Visit v
			    where v.patient.id = :patientId
			      and (
			          v.date < :today
			          or (v.date = :today and v.time <= :now)
			      )
			    order by v.date desc, v.time desc
			""")
	Page<Visit> findPastByPatient(@Param("patientId") Long patientId, @Param("today") LocalDate today,
			@Param("now") LocalTime now, Pageable pageable);

}
