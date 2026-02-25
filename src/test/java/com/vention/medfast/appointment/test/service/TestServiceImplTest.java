package com.vention.medfast.appointment.test.service;

import com.vention.medfast.appointment.dto.LabResultDTO;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.dto.TestCreateWithLabsDto;
import com.vention.medfast.appointment.mapper.TestMapper;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.Test;
import com.vention.medfast.appointment.repository.TestRepository;
import com.vention.medfast.appointment.service.TestServiceImpl;
import com.vention.medfast.appointment.service.cache.TestCacheService;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.clinic.service.ClinicService;
import com.vention.medfast.labresult.model.LabResultStatus;
import com.vention.medfast.labresult.service.LabResultService;
import com.vention.medfast.labtest.model.LabTest;
import com.vention.medfast.labtest.service.LabTestService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.wrapper.CachedPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

	@Mock
	private TestRepository testRepository;

	@Mock
	private PatientService patientService;

	@Mock
	private LabTestService labTestService;

	@Mock
	private LabResultService labResultService;

	@Mock
	private ClinicService clinicService;

	@Mock
	private TestMapper testMapper;

	@Mock
	private TestCacheService testCacheService;

	@InjectMocks
	private TestServiceImpl testService;

	@org.junit.jupiter.api.Test
	@DisplayName("Should return patient lab history page")
	void shouldReturnPatientLabHistory() {
		Long userId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		LabResultDTO labDto = new LabResultDTO(100L, "Blood Test", LabResultStatus.READY, "url", LocalDateTime.now());

		PatientLabHistoryDTO dto = new PatientLabHistoryDTO(10L, LocalDate.now(), AppointmentStatus.COMPLETED.name(),
				List.of(labDto));

		CachedPage<PatientLabHistoryDTO> cachedPage = new CachedPage<>(List.of(dto), 0, 10, 1);

		when(testCacheService.getPatientLabHistory(userId, 0, 10)).thenReturn(cachedPage);

		Page<PatientLabHistoryDTO> result = testService.getPatientLabHistory(userId, pageable);

		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getTotalElements()).isEqualTo(1);

		PatientLabHistoryDTO resultDto = result.getContent().get(0);

		assertThat(resultDto.appointmentId()).isEqualTo(10L);
		assertThat(resultDto.tests()).hasSize(1);
		assertThat(resultDto.tests().get(0).testName()).isEqualTo("Blood Test");

		verify(testCacheService).getPatientLabHistory(userId, 0, 10);
	}

	@org.junit.jupiter.api.Test
	@DisplayName("Should create appointment with labs correctly")
	void shouldCreateAppointmentWithLabs() {
		TestCreateWithLabsDto dto = new TestCreateWithLabsDto(100L, 5L, LocalDate.now().plusDays(1), LocalTime.of(9, 0),
				List.of(1L, 2L));

		Patient patient = new Patient();
		patient.setId(100L);

		Clinic clinic = new Clinic();
		clinic.setId(5L);

		LabTest labTest1 = new LabTest();
		LabTest labTest2 = new LabTest();
		List<LabTest> labTests = List.of(labTest1, labTest2);

		Test testAppointment = new Test();

		when(patientService.getById(dto.patientId())).thenReturn(patient);
		when(clinicService.findById(dto.clinicId())).thenReturn(clinic);
		when(labTestService.findAllById(any(Set.class))).thenReturn(labTests);
		when(testMapper.toLabAppointment(dto, patient, clinic)).thenReturn(testAppointment);

		testService.createWithLabs(dto);

		assertThat(testAppointment.getType()).isEqualTo(AppointmentType.TEST);
		assertThat(testAppointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED_CONFIRMED);

		verify(patientService).getById(dto.patientId());
		verify(clinicService).findById(dto.clinicId());
		verify(labTestService).findAllById(any(Set.class));
		verify(testMapper).toLabAppointment(dto, patient, clinic);

		verify(testRepository).save(testAppointment);
		verify(labResultService).createForAppointment(testAppointment, labTests);
	}

}
