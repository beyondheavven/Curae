package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	@EntityGraph(attributePaths = { "labResults", "labResults.labTest" })
	@Query(value = """
			    SELECT DISTINCT t
			    FROM Test t
			    JOIN t.labResults lr
			    WHERE t.patient.user.id = :userId
			""", countQuery = """
			    SELECT COUNT(DISTINCT t.id)
			    FROM Test t
			    JOIN t.labResults lr
			    WHERE t.patient.user.id = :userId
			""")
	Page<Appointment> findLabHistoryPage(@Param("userId") Long userId, Pageable pageable);

	@Query("""
			    SELECT new com.vention.medfast.statistics.dto.response.AppointmentStatusCounts(
			        COUNT(a),
			        COALESCE(SUM(CASE WHEN a.status = 'SCHEDULED' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN a.status = 'MISSED'    THEN 1 ELSE 0 END), 0)
			    )
			    FROM Appointment a
			    WHERE a.date BETWEEN :startDate AND :endDate
			""")
	AppointmentStatusCounts getAllStatusStatistics(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	@Query("""
			    SELECT NEW com.vention.medfast.statistics.dto.response.AppointmentStatusCounts(
			        COUNT(a),
			        COALESCE(SUM(CASE WHEN a.status = 'SCHEDULED' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END), 0),
			        COALESCE(sum(CASE WHEN a.status = 'MISSED' THEN 1 ELSE 0 END), 0)
			        )
			    FROM Appointment a
			    WHERE a.date BETWEEN :startDate AND :endDate
			        AND a.type = :type
			""")
	AppointmentStatusCounts getStatusStatisticsByType(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate, @Param("type") AppointmentType type);

}
