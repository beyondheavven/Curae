package com.vention.medfast.doctor.repository;

import com.vention.medfast.doctor.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

	Optional<Doctor> findByUserId(Long userId);

}
