package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

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
			    SELECT new com.vention.medfast.statistics.dto.response.AppointmentStatusCounts(
			        COUNT(a),
			        COALESCE(SUM(CASE WHEN a.status = 'SCHEDULED' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN a.status = 'MISSED'    THEN 1 ELSE 0 END), 0)
			    )
			    FROM Appointment a
			    WHERE a.date BETWEEN :startDate AND :endDate
			      AND a.type = :type
			""")
	AppointmentStatusCounts getStatusStatisticsByType(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate, @Param("type") AppointmentType type);

}
