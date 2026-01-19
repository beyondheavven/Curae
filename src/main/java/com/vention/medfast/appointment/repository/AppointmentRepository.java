package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	@EntityGraph(attributePaths = { "labResults", "labResults.labTest" })
	@Query(value = """
			    SELECT DISTINCT a
			    FROM Appointment a
			    JOIN a.labResults lr
			    WHERE a.patient.user.id = :userId
			""", countQuery = """
			    SELECT COUNT(DISTINCT a.id)
			    FROM Appointment a
			    JOIN a.labResults lr
			    WHERE a.patient.user.id = :userId
			""")
	Page<Appointment> findLabHistoryPage(@Param("userId") Long userId, Pageable pageable);

}
