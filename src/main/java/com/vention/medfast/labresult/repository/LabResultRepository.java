package com.vention.medfast.labresult.repository;

import com.vention.medfast.labresult.model.LabResult;
import com.vention.medfast.statistics.dto.response.LabResultStatusCounts;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, Long> {

	@Query("""
			SELECT new com.vention.medfast.statistics.dto.response.LabResultStatusCounts(
			        COALESCE(SUM(CASE WHEN lr.status = 'PROCESSING' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN lr.status = 'READY' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN lr.status = 'FAILED' THEN 1 ELSE 0 END), 0)
			        )
			FROM LabResult lr
			JOIN lr.test t
			WHERE t.date BETWEEN :startDate and :endDate
			""")
	LabResultStatusCounts getStatusCounts(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query("""
			    SELECT new com.vention.medfast.statistics.dto.response.LabResultStatusCounts(
			        COALESCE(SUM(CASE WHEN lr.status = 'PROCESSING' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN lr.status = 'READY' THEN 1 ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN lr.status = 'FAILED' THEN 1 ELSE 0 END), 0)
			    )
			    FROM LabResult lr
			    JOIN lr.test t
			    WHERE t.clinic.id = :clinicId
			      AND t.date BETWEEN :startDate and :endDate
			""")
	LabResultStatusCounts getStatusCountsByClinic(@Param("clinicId") Long clinicId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
