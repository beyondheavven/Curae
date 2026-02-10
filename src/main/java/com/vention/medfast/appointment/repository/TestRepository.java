package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.dto.TestHistoryRow;
import com.vention.medfast.appointment.model.Test;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {

	@Query("""
			    select new com.vention.medfast.appointment.dto.TestHistoryRow(
			        t.id,
			        t.date,
			        t.status
			    )
			    from Test t
			    where t.patient.user.id = :userId
			    order by t.date desc
			""")
	Page<TestHistoryRow> findLabHistoryRows(@Param("userId") Long userId, Pageable pageable);

	@Query("""
			    SELECT new com.vention.medfast.statistics.dto.response.AppointmentStatusCounts(
			        COUNT(t),
			        COALESCE(SUM(CASE WHEN t.status = 'SCHEDULED' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN t.status = 'CANCELLED' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN t.status = 'MISSED' THEN 1 ELSE 0 END), 0)
			    )
			    FROM Test t
			    WHERE t.clinic.id = :clinicId
			      AND t.date BETWEEN :startDate AND :endDate
			""")
	AppointmentStatusCounts getStatusStatisticsByClinic(@Param("clinicId") Long clinicId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
