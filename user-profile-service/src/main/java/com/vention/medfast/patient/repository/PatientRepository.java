package com.vention.medfast.patient.repository;

import com.vention.medfast.patient.model.Patient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

	Optional<Patient> findByUserId(Long userId);

	boolean existsByUserId(Long userId);

}
