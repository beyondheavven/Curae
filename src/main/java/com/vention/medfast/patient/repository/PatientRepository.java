package com.vention.medfast.patient.repository;

import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

	Optional<Patient> findByUserId(Long userId);

	boolean existsByUser(User user);

}
