package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.LabResultDTO;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.dto.StatusStatisticDto;
import com.vention.medfast.appointment.dto.TestCreateWithLabsDto;
import com.vention.medfast.appointment.event.AppointmentEventPublisher;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.LabResult;
import com.vention.medfast.appointment.model.LabResultStatus;
import com.vention.medfast.appointment.model.LabTest;
import com.vention.medfast.appointment.model.Test;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.appointment.repository.LabResultRepository;
import com.vention.medfast.appointment.repository.LabTestRepository;
import com.vention.medfast.appointment.repository.TestRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

	private final AppointmentRepository appointmentRepository;

	private final TestRepository testRepository;

	private final LabTestRepository labTestRepository;

	private final LabResultRepository labResultRepository;

	private final AppointmentEventPublisher eventPublisher;

	@Override
	@Transactional
	public Long createWithLabs(TestCreateWithLabsDto dto) {
		Appointment appointment = new Appointment();
		appointment.setPatientId(dto.patientId());
		appointment.setType(AppointmentType.TEST);
		appointment.setStatus(AppointmentStatus.COMPLETED);
		appointment.setScheduledDate(dto.date());
		appointment.setScheduledTime(dto.time());
		Appointment savedAppointment = appointmentRepository.save(appointment);

		Test test = new Test();
		test.setAppointmentId(savedAppointment.getId());
		test.setClinicId(dto.clinicId());
		testRepository.save(test);

		List<Long> labTestIds = dto.labTestIds() == null ? List.of() : dto.labTestIds();
		List<LabTest> labTests = labTestRepository.findAllById(labTestIds);
		if (labTests.size() != labTestIds.size()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown lab test id");
		}
		List<LabResult> results = new ArrayList<>();
		for (LabTest labTest : labTests) {
			LabResult result = new LabResult();
			result.setTestId(savedAppointment.getId());
			result.setLabTestId(labTest.getId());
			result.setStatus(LabResultStatus.PROCESSING);
			results.add(result);
		}
		labResultRepository.saveAll(results);
		eventPublisher.appointmentCreated(savedAppointment, null);
		return savedAppointment.getId();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PatientLabHistoryDTO> getPatientLabHistory(Long patientId, Pageable pageable) {
		Page<Appointment> appointments = appointmentRepository
			.findByPatientIdAndTypeAndStatusInOrderByScheduledDateDescScheduledTimeDesc(patientId, AppointmentType.TEST,
					List.of(AppointmentStatus.COMPLETED, AppointmentStatus.SCHEDULED,
							AppointmentStatus.SCHEDULED_CONFIRMED),
					pageable);
		return appointments.map(appointment -> {
			List<LabResultDTO> tests = labResultRepository.findByTestId(appointment.getId())
				.stream()
				.map(result -> new LabResultDTO(result.getId(),
						labTestRepository.findById(result.getLabTestId()).map(LabTest::getName).orElse(null),
						result.getStatus(), result.getResultUrl(), result.getUploadedAt()))
				.toList();
			return new PatientLabHistoryDTO(appointment.getId(), appointment.getScheduledDate(), tests);
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<StatusStatisticDto> getStatusStatisticsByClinic() {
		return testRepository.countByClinic()
			.stream()
			.map(row -> new StatusStatisticDto(String.valueOf(row[0]), ((Number) row[1]).longValue()))
			.toList();
	}

}
