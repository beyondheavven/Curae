package com.vention.medfast.feedback.clinic.repository;

import com.vention.medfast.feedback.clinic.model.ClinicFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClinicFeedbackRepository extends JpaRepository<ClinicFeedback, Long> {

	Page<ClinicFeedback> findAllByClinicId(Long clinicId, Pageable pageable);

	@Query("""
			SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
			FROM ClinicFeedback f
			WHERE f.patient.id = :patientId
			  AND f.clinic.id = :clinicId
			""")
	boolean existsByPatientIdAndClinicId(@Param("patientId") Long patientId, @Param("clinicId") Long clinicId);

	@Query("""
			SELECT COALESCE(AVG(f.rating), 0)
			FROM ClinicFeedback f
			WHERE f.clinic.id = :clinicId
			""")
	double findAvgRatingByClinicId(@Param("clinicId") Long clinicId);

}
