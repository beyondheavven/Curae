package com.vention.medfast.appointment.repository;

import com.vention.medfast.appointment.model.LabResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabResultRepository extends JpaRepository<LabResult, Long> {

	List<LabResult> findByTestId(Long testId);

}
