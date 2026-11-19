package com.vention.curae.appointment.repository;

import com.vention.curae.appointment.model.LabResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabResultRepository extends JpaRepository<LabResult, Long> {

	List<LabResult> findByTestId(Long testId);

}
