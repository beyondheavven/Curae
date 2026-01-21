package com.vention.medfast.visit.repository;

import com.vention.medfast.visit.model.Visit;
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
			    join v.appointment a
			    where a.patient.id = :patientId
			      and (
			          a.date > :today
			          or (a.date = :today and a.time > :now)
			      )
			    order by a.date, a.time
			""")
	Page<Visit> findUpcomingByPatient(@Param("patientId") Long patientId, @Param("today") LocalDate today,
			@Param("now") LocalTime now, Pageable pageable);

	@Query("""
			    select v from Visit v
			    join v.appointment a
			    where a.patient.id = :patientId
			      and (
			          a.date < :today
			          or (a.date = :today and a.time <= :now)
			      )
			    order by a.date desc, a.time desc
			""")
	Page<Visit> findPastByPatient(@Param("patientId") Long patientId, @Param("today") LocalDate today,
			@Param("now") LocalTime now, Pageable pageable);

}
