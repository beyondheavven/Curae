package com.vention.curae.appointment.repository;

import com.vention.curae.appointment.model.Test;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TestRepository extends JpaRepository<Test, Long> {

	Optional<Test> findByAppointmentId(Long appointmentId);

	@Query("select t.clinicId, count(t) from Test t where t.clinicId is not null group by t.clinicId")
	List<Object[]> countByClinic();

}
