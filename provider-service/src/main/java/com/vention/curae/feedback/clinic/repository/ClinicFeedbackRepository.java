package com.vention.curae.feedback.clinic.repository;

import com.vention.curae.feedback.clinic.model.ClinicFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClinicFeedbackRepository extends JpaRepository<ClinicFeedback, Long> {

	Page<ClinicFeedback> findAllByClinicId(Long clinicId, Pageable pageable);

	boolean existsByPatientIdAndClinicId(Long patientId, Long clinicId);

	@Query("""
			SELECT COALESCE(AVG(f.rating), 0)
			FROM ClinicFeedback f
			WHERE f.clinic.id = :clinicId
			""")
	double findAvgRatingByClinicId(@Param("clinicId") Long clinicId);

}