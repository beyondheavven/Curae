package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.mapper.AppointmentMapper;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.labresult.mapper.LabResultMapper;
import com.vention.medfast.labresult.service.LabResultService;
import com.vention.medfast.labtest.model.LabTest;
import com.vention.medfast.labtest.service.LabTestService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private LabResultMapper labResultMapper;

	@Mock
	private AppointmentMapper appointmentMapper;

	@Mock
	private PatientService patientService;

	@Mock
	private LabTestService labTestService;

	@Mock
	private LabResultService labResultService;

	@InjectMocks
	private AppointmentServiceImpl appointmentService;

	@Test
	void shouldReturnLabHistoryForUser() {
		Pageable pageable = Pageable.unpaged();

		Appointment appointment = new Appointment();
		appointment.setId(5L);

		PatientLabHistoryDTO expectedDto = new PatientLabHistoryDTO(5L, LocalDate.now(), "COMPLETED", List.of());

		when(appointmentRepository.findLabHistoryPage(7L, pageable)).thenReturn(new PageImpl<>(List.of(appointment)));
		when(labResultMapper.toHistoryDto(appointment)).thenReturn(expectedDto);

		Page<PatientLabHistoryDTO> result = appointmentService.getPatientLabHistory(7L, pageable);

		assertThat(result).hasSize(1);
		assertThat(result.getContent()).containsExactly(expectedDto);

		verify(appointmentRepository).findLabHistoryPage(7L, pageable);
		verify(labResultMapper).toHistoryDto(appointment);
	}

	@Test
	void shouldCreateAppointmentWithLabsWhenInputIsValid() {
		AppointmentCreateWithLabsDto req = new AppointmentCreateWithLabsDto(2L, LocalDate.of(2031, 1, 1),
				LocalTime.of(12, 0), List.of(10L, 10L, 11L));

		Patient patient = new Patient();
		patient.setId(2L);

		LabTest firstTest = new LabTest();
		firstTest.setId(10L);
		LabTest secondTest = new LabTest();
		secondTest.setId(11L);

		Appointment appointment = new Appointment();
		appointment.setId(100L);

		when(patientService.getById(2L)).thenReturn(patient);
		when(labTestService.findAllById(Set.of(10L, 11L))).thenReturn(List.of(firstTest, secondTest));
		when(appointmentMapper.toEntity(req, patient)).thenReturn(appointment);

		appointmentService.createWithLabs(req);

		ArgumentCaptor<Set<Long>> captor = ArgumentCaptor.forClass(Set.class);
		verify(labTestService).findAllById(captor.capture());
		assertThat(captor.getValue()).containsExactlyInAnyOrder(10L, 11L);

		verify(appointmentRepository).save(appointment);
		verify(labResultService).createForAppointment(appointment, List.of(firstTest, secondTest));
		verify(patientService, times(1)).getById(2L);
	}

}
