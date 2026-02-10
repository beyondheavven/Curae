package com.vention.medfast.feedback.doctor.repository;

import com.vention.medfast.feedback.doctor.model.DoctorFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DoctorFeedbackRepository extends JpaRepository<DoctorFeedback, Long> {

	Page<DoctorFeedback> findAllByDoctorId(Long doctorId, Pageable pageable);

	@Query("""
			SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
			FROM DoctorFeedback f
			WHERE f.patient.id = :patientId
			  AND f.doctor.id = :doctorId
			""")
	boolean existsByPatientIdAndDoctorId(@Param("patientId") Long patientId, @Param("doctorId") Long doctorId);

	@Query("""
			SELECT COALESCE(AVG(f.rating), 0)
			FROM DoctorFeedback f
			WHERE f.doctor.id = :doctorId
			""")
	double findAvgRatingByDoctorId(@Param("doctorId") Long doctorId);

}
