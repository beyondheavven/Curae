package com.vention.medfast.labresult.service;

import com.vention.medfast.labresult.model.LabResult;
import com.vention.medfast.labresult.model.LabResultStatus;
import com.vention.medfast.labresult.repository.LabResultRepository;
import com.vention.medfast.labtest.model.LabTest;

import java.time.LocalDate;
import java.util.List;

import com.vention.medfast.statistics.dto.response.LabResultStatusCounts;
import com.vention.medfast.test.model.Test;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class LabResultServiceImpl implements LabResultService {

	private final LabResultRepository labResultRepository;

	@Override
	public void createForAppointment(Test test, List<LabTest> tests) {
		List<LabResult> results = tests.stream().map(labTest -> {
			LabResult lr = new LabResult();
			lr.setTest(test);
			lr.setLabTest(labTest);
			lr.setStatus(LabResultStatus.PROCESSING);
			return lr;
		}).toList();

		labResultRepository.saveAll(results);
	}

	@Override
	@Transactional(readOnly = true)
	public LabResultStatusCounts getStatusStatistics(LocalDate startDate, LocalDate endDate) {
		return labResultRepository.getStatusCounts(startDate, endDate);
	}

}
