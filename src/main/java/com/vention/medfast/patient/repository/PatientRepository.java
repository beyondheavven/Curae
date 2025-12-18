package com.vention.medfast.patient.repository;

import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.user.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

	boolean existsByUser(User user);

	Optional<Patient> findByUserId(Long userId);

}
