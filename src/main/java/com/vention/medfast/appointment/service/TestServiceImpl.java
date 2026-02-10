package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.*;
import com.vention.medfast.appointment.mapper.TestMapper;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.Test;
import com.vention.medfast.appointment.repository.TestRepository;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.clinic.service.ClinicService;
import com.vention.medfast.labresult.repository.LabResultRepository;
import com.vention.medfast.labresult.service.LabResultService;
import com.vention.medfast.labtest.model.LabTest;
import com.vention.medfast.labtest.service.LabTestService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

	private final TestRepository testRepository;

	private final LabResultRepository labResultRepository;

	private final PatientService patientService;

	private final LabTestService labTestService;

	private final LabResultService labResultService;

	private final ClinicService clinicService;

	private final TestMapper testMapper;

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "patientTestHistory",
			key = "#userId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
	public Page<PatientLabHistoryDTO> getPatientLabHistory(Long userId, Pageable pageable) {
		Page<TestHistoryRow> tests = testRepository.findLabHistoryRows(userId, pageable);

		if (tests.isEmpty()) {
			return Page.empty(pageable);
		}

		List<Long> testIds = tests.stream().map(TestHistoryRow::testId).toList();

		List<LabResultRow> labResults = labResultRepository.findHistoryRowsByTestIds(testIds);

		Map<Long, List<LabResultDTO>> resultsByTestId = labResults.stream()
			.collect(Collectors.groupingBy(LabResultRow::testId,
					Collectors.mapping(lr -> new LabResultDTO(lr.labResultId(), lr.testName(), lr.status(),
							lr.resultUrl(), lr.uploadedAt()), Collectors.toList())));

		return tests.map(test -> new PatientLabHistoryDTO(test.testId(), test.date(), test.status().name(),
				resultsByTestId.getOrDefault(test.testId(), List.of())));
	}

	@Override
	@Transactional
	public void createWithLabs(TestCreateWithLabsDto req) {
		Patient patient = patientService.getById(req.patientId());
		Clinic clinic = clinicService.findById(req.clinicId());

		Set<Long> uniqueIds = new LinkedHashSet<>(req.labTestIds());
		List<LabTest> labTests = labTestService.findAllById(uniqueIds);

		Test testAppointment = testMapper.toLabAppointment(req, patient, clinic);
		testAppointment.setType(AppointmentType.TEST);
		testAppointment.setStatus(AppointmentStatus.SCHEDULED_CONFIRMED);

		testRepository.save(testAppointment);
		labResultService.createForAppointment(testAppointment, labTests);
	}

	@Override
	public AppointmentStatusCounts getStatusStatisticsByClinic(Long clinicId, LocalDate startDate, LocalDate endDate) {
		return testRepository.getStatusStatisticsByClinic(clinicId, startDate, endDate);
	}

}
