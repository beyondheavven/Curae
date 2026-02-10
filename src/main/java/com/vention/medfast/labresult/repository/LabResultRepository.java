package com.vention.medfast.labresult.repository;

import com.vention.medfast.appointment.dto.LabResultRow;
import com.vention.medfast.labresult.model.LabResult;
import com.vention.medfast.statistics.dto.response.LabResultStatusCounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

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
			    select new com.vention.medfast.appointment.dto.LabResultRow(
			        t.id,
			        lr.id,
			        lt.name,
			        lr.status,
			        lr.resultUrl,
			        lr.uploadedAt
			    )
			    from LabResult lr
			    join lr.test t
			    join lr.labTest lt
			    where t.id in :testIds
			""")
	List<LabResultRow> findHistoryRowsByTestIds(@Param("testIds") Collection<Long> testIds);

}
