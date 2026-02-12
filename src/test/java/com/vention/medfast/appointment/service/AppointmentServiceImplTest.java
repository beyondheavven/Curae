package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentUpdateDto;
import com.vention.medfast.appointment.mapper.AppointmentMapper;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.Visit;
import com.vention.medfast.appointment.referral.model.Referral;
import com.vention.medfast.appointment.referral.model.ReferralStatus;
import com.vention.medfast.appointment.referral.service.ReferralService;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.appointment.state.AppointmentStateContext;
import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import com.vention.medfast.user.model.RoleType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
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
	private AppointmentMapper appointmentMapper;

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

	@Test
	void shouldReturnTrueWhenPatientIsOwnerOfAppointment() {
		Long appointmentId = 1L;
		Long patientId = 10L;
		Patient patient = new Patient();
		patient.setId(patientId);
		Appointment appointment = new Appointment();
		appointment.setPatient(patient);

		when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

		boolean result = appointmentService.isOwner(appointmentId, patientId);

		assertThat(result).isTrue();
	}

	@Test
	void shouldReturnTrueWhenPatientAccessesOwnAppointmentInExtendedCheck() {
		Long appointmentId = 1L;
		CustomUserDetails principal = new CustomUserDetails(1L, "u", "p", RoleType.PATIENT, 10L, null);

		Patient patient = new Patient();
		patient.setId(10L);
		Appointment appointment = new Appointment();
		appointment.setPatient(patient);

		when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

		boolean result = appointmentService.isOwnerExtended(appointmentId, principal);

		assertThat(result).isTrue();
	}

	@Test
	void shouldReturnTrueWhenDoctorAccessesAssignedVisitInExtendedCheck() {
		Long appointmentId = 1L;
		CustomUserDetails principal = new CustomUserDetails(2L, "d", "p", RoleType.DOCTOR, null, 20L);

		Doctor doctor = new Doctor();
		doctor.setId(20L);
		Visit visit = new Visit();
		visit.setPatient(new Patient());
		visit.getPatient().setId(99L);
		visit.setDoctor(doctor);

		when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(visit));

		boolean result = appointmentService.isOwnerExtended(appointmentId, principal);

		assertThat(result).isTrue();
	}

	@Test
    void shouldReturnFalseWhenAppointmentNotFoundInExtendedCheck() {
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        boolean result = appointmentService.isOwnerExtended(1L,
            new CustomUserDetails(1L, "u", "p", RoleType.PATIENT, 10L, null));

        assertThat(result).isFalse();
    }

}
