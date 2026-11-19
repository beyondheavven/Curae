package com.vention.curae.appointment.repository;

import com.vention.curae.appointment.model.Visit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VisitRepository extends JpaRepository<Visit, Long> {

	Optional<Visit> findByAppointmentId(Long appointmentId);

	@Query("select v.clinicId, count(v) from Visit v group by v.clinicId")
	List<Object[]> countByClinic();

	@Query("select v.doctorId, count(v) from Visit v where v.doctorId is not null group by v.doctorId")
	List<Object[]> countByDoctor();

}
