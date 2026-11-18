package com.vention.medfast.doctor.repository;

import com.vention.medfast.doctor.model.Doctor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {

	Optional<Doctor> findByUserId(Long userId);

	boolean existsByUserId(Long userId);

	@Query("SELECT d.id FROM Doctor d WHERE d.userId = :userId")
	Optional<Long> findIdByUserId(@Param("userId") Long userId);

}
