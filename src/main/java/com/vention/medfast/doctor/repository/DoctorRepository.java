package com.vention.medfast.doctor.repository;

import com.vention.medfast.doctor.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {

	Optional<Doctor> findByUserId(Long userId);

	@Query("SELECT d.id FROM Doctor d WHERE d.user.id = :userId")
	Optional<Long> findIdByUserId(Long userId);

}
