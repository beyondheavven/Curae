package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentUpdateDto;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.mapper.AppointmentMapper;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.referral.model.Referral;
import com.vention.medfast.referral.model.ReferralStatus;
import com.vention.medfast.referral.service.ReferralService;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.appointment.state.AppointmentStateContext;
import com.vention.medfast.labresult.mapper.LabResultMapper;
import com.vention.medfast.labresult.service.LabResultService;
import com.vention.medfast.labtest.model.LabTest;
import com.vention.medfast.labtest.service.LabTestService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private ReferralService referralService;

	@Mock
	private PatientService patientService;

	@Mock
	private AppointmentMapper appointmentMapper;

	@Mock
	private LabTestService labTestService;

	@Mock
	private LabResultService labResultService;

	@Mock
	private LabResultMapper labResultMapper;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private AppointmentStateContext stateContext;

	@InjectMocks
	private AppointmentServiceImpl appointmentService;

	@Test
	void shouldCreateDraftWithReferral() {
		AppointmentCreateDto dto = new AppointmentCreateDto(10L, LocalDate.now().plusDays(1), LocalTime.of(10, 0));
		Referral referral = new Referral();
		referral.setAppointmentType(AppointmentType.VISIT);
		Appointment appointment = new Appointment();

		when(referralService.getActiveById(10L)).thenReturn(referral);
		when(appointmentMapper.toAppointment(dto, referral.getPatient(), referral)).thenReturn(appointment);
		when(appointmentRepository.save(appointment)).thenReturn(appointment);
		appointmentService.create(dto);
		assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
		assertThat(appointment.getType()).isEqualTo(AppointmentType.VISIT);
		verify(appointmentRepository).save(appointment);
	}

	@Test
	void shouldConfirmAppointmentAndMarkReferralUsed() {
		Referral referral = new Referral();
		referral.setStatus(ReferralStatus.ACTIVE);
		Appointment appointment = new Appointment();
		appointment.setStatus(AppointmentStatus.SCHEDULED);
		appointment.setReferral(referral);

		when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
		when(stateContext.isTransitionAllowed(AppointmentStatus.SCHEDULED, AppointmentStatus.SCHEDULED_CONFIRMED))
			.thenReturn(true);
		appointmentService.confirm(1L);

		assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED_CONFIRMED);
		verify(referralService).markUsed(referral);
	}

	@Test
	void shouldThrowException_whenConfirmingNotDraftAppointment() {
		Appointment appointment = new Appointment();
		appointment.setStatus(AppointmentStatus.COMPLETED);

		when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
		when(stateContext.isTransitionAllowed(AppointmentStatus.COMPLETED, AppointmentStatus.SCHEDULED_CONFIRMED))
			.thenReturn(false);

		assertThatThrownBy(() -> appointmentService.confirm(1L)).isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Cannot confirm appointment in status COMPLETED");
	}

	@Test
	void shouldReturnAllStatusStatistics() {
		LocalDate startDate = LocalDate.of(2026, 1, 1);
		LocalDate endDate = LocalDate.of(2026, 1, 31);
		AppointmentStatusCounts expectedCounts = new AppointmentStatusCounts(10L, 5L, 2L, 3L, 0L);

		when(appointmentRepository.getAllStatusStatistics(startDate, endDate)).thenReturn(expectedCounts);

		AppointmentStatusCounts result = appointmentService.getAllStatusStatistics(startDate, endDate);

		assertThat(result).isNotNull();
		assertThat(result.total()).isEqualTo(10L);
		assertThat(result.scheduled()).isEqualTo(5L);
		verify(appointmentRepository).getAllStatusStatistics(startDate, endDate);
	}

	@Test
	void shouldReturnStatusStatisticsByType() {
		LocalDate startDate = LocalDate.of(2026, 1, 1);
		LocalDate endDate = LocalDate.of(2026, 1, 31);
		AppointmentType type = AppointmentType.VISIT;
		AppointmentStatusCounts expectedCounts = new AppointmentStatusCounts(5L, 2L, 1L, 2L, 0L);

		when(appointmentRepository.getStatusStatisticsByType(startDate, endDate, type)).thenReturn(expectedCounts);

		AppointmentStatusCounts result = appointmentService.getStatusStatisticsByType(startDate, endDate, type);

		assertThat(result).isNotNull();
		assertThat(result.total()).isEqualTo(5L);
		verify(appointmentRepository).getStatusStatisticsByType(startDate, endDate, type);
	}

	@Test
	void shouldReturnZeroCountsWhenNoAppointmentsExist() {
		LocalDate startDate = LocalDate.of(2099, 1, 1);
		LocalDate endDate = LocalDate.of(2099, 1, 31);
		AppointmentStatusCounts emptyCounts = new AppointmentStatusCounts(0L, 0L, 0L, 0L, 0L);

		when(appointmentRepository.getAllStatusStatistics(startDate, endDate)).thenReturn(emptyCounts);

		AppointmentStatusCounts result = appointmentService.getAllStatusStatistics(startDate, endDate);

		assertThat(result).isNotNull();
		assertThat(result.total()).isZero();
	}

	@Test
	void shouldUpdateAppointment_whenStatusIsScheduled() {
		Appointment appointment = new Appointment();
		appointment.setId(1L);
		appointment.setStatus(AppointmentStatus.SCHEDULED);

		Patient patient = new Patient();
		patient.setId(10L);
		appointment.setPatient(patient);

		AppointmentUpdateDto dto = new AppointmentUpdateDto(LocalDate.now(), LocalTime.now(),
				AppointmentStatus.SCHEDULED);

		when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
		when(stateContext.isEditable(AppointmentStatus.SCHEDULED)).thenReturn(true);

		Long result = appointmentService.update(dto, 1L);

		assertEquals(1L, result);
		verify(appointmentMapper).updateFromDto(dto, appointment);
	}

	@Test
	void shouldThrowIllegalStateException_whenStatusIsCANCELLED() {
		Appointment appointment = new Appointment();
		appointment.setId(1L);
		appointment.setStatus(AppointmentStatus.CANCELLED);

		when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

		AppointmentUpdateDto dto = new AppointmentUpdateDto(LocalDate.now(), LocalTime.now(),
				AppointmentStatus.CANCELLED);

		when(stateContext.isEditable(AppointmentStatus.CANCELLED)).thenReturn(false);

		IllegalStateException e = assertThrows(IllegalStateException.class, () -> appointmentService.update(dto, 1L));
		assertEquals("Appointment cannot be edited in status CANCELLED", e.getMessage());
		verifyNoInteractions(appointmentMapper, eventPublisher);
	}

}
