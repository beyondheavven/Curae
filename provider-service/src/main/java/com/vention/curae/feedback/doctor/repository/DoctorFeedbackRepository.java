package com.vention.curae.feedback.doctor.repository;

import com.vention.curae.feedback.doctor.model.DoctorFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DoctorFeedbackRepository extends JpaRepository<DoctorFeedback, Long> {

	Page<DoctorFeedback> findAllByDoctorId(Long doctorId, Pageable pageable);

	boolean existsByPatientIdAndDoctorId(Long patientId, Long doctorId);

	@Query("""
			SELECT COALESCE(AVG(f.rating), 0)
			FROM DoctorFeedback f
			WHERE f.doctor.id = :doctorId
			""")
	double findAvgRatingByDoctorId(@Param("doctorId") Long doctorId);

}