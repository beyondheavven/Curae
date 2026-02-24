package com.vention.medfast.appointment.service.cache;

import com.vention.medfast.appointment.dto.LabResultDTO;
import com.vention.medfast.appointment.dto.LabResultRow;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.dto.TestHistoryRow;
import com.vention.medfast.appointment.repository.TestRepository;
import com.vention.medfast.labresult.repository.LabResultRepository;
import com.vention.medfast.wrapper.CachedPage;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestCacheService {

	private final TestRepository testRepository;

	private final LabResultRepository labResultRepository;

	@Cacheable(cacheNames = "patientTestHistory", key = "'patient:' + #userId + ':page:' + #page + ':size:' + #size",
			sync = true)
	public CachedPage<PatientLabHistoryDTO> getPatientLabHistory(Long userId, int page, int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

		Page<TestHistoryRow> tests = testRepository.findLabHistoryRows(userId, pageable);

		if (tests.isEmpty()) {
			return new CachedPage<>(List.of(), page, size, 0);
		}

		List<Long> testIds = tests.stream().map(TestHistoryRow::testId).toList();

		List<LabResultRow> labResults = labResultRepository.findHistoryRowsByTestIds(testIds);

		Map<Long, List<LabResultDTO>> resultsByTestId = labResults.stream()
			.collect(Collectors.groupingBy(LabResultRow::testId,
					Collectors.mapping(lr -> new LabResultDTO(lr.labResultId(), lr.testName(), lr.status(),
							lr.resultUrl(), lr.uploadedAt()), Collectors.toList())));

		List<PatientLabHistoryDTO> content = tests.stream()
			.map(test -> new PatientLabHistoryDTO(test.testId(), test.date(), test.status().name(),
					resultsByTestId.getOrDefault(test.testId(), List.of())))
			.toList();

		return new CachedPage<>(content, page, size, tests.getTotalElements());
	}

}
