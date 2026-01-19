package com.vention.medfast.labtest.service;

import com.vention.medfast.labtest.model.LabTest;
import com.vention.medfast.labtest.repository.LabTestRepository;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LabTestServiceImpl implements LabTestService {

	private final LabTestRepository labTestRepository;

	@Override
	public List<LabTest> findAllById(Set<Long> labTestIds) {
		List<LabTest> tests = labTestRepository.findAllById(labTestIds);

		if (tests.size() != labTestIds.size()) {
			throw new IllegalArgumentException("Some lab_test_ids are invalid");
		}

		return tests;
	}

}
