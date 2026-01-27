package com.vention.medfast.visit.service;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.dto.VisitResponseDto;
import com.vention.medfast.visit.model.Visit;
import com.vention.medfast.visit.model.VisitType;
import com.vention.medfast.visit.repository.VisitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitServiceImplTest {

	@Mock
	private VisitRepository visitRepository;

	@Mock
	private VisitMapper visitMapper;

	@Mock
	private AppointmentService appointmentService;

	@Mock
	private PatientService patientService;

	@Mock
	private Authentication authentication;

	@Mock
	private SecurityContext securityContext;

	@InjectMocks
	private VisitServiceImpl visitService;

	@Test
	public void shouldCreateVisitWhenInputIsValid() {
		VisitCreateDto dto = new VisitCreateDto(1L, "Diagnosis", "Symptoms", "Medical report");
		Appointment appointment = new Appointment();
		appointment.setId(1L);
		appointment.setStatus(AppointmentStatus.SCHEDULED_CONFIRMED);
		appointment.setType(AppointmentType.VISIT);
		Visit visit = new Visit();
		visit.setAppointment(appointment);
		when(appointmentService.getConfirmed(1L)).thenReturn(appointment);
		when(visitRepository.existsById(1L)).thenReturn(false);
		when(visitMapper.toEntity(dto, appointment)).thenReturn(visit);
		when(visitRepository.save(any())).thenAnswer(inv -> {
			Visit v = inv.getArgument(0);
			v.setId(1L);
			return v;
		});
		long result = visitService.create(dto);
		assertEquals(1L, result);
		assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
		verify(appointmentService).getConfirmed(1L);
		verify(visitRepository).existsById(1L);
		verify(visitMapper).toEntity(dto, appointment);
		verify(visitRepository).save(visit);
	}

	@Test
	void shouldReturnUpcomingVisitsForPatient() {
		String email = "patient@mail.com";
		Patient patient = new Patient();
		patient.setId(1L);
		Visit visit = new Visit();
		VisitResponseDto responseDto = mock(VisitResponseDto.class);
		Pageable pageable = mock(Pageable.class);
		Page<Visit> page = new PageImpl<>(List.of(visit));
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(authentication.getName()).thenReturn(email);
		when(patientService.getPatientByEmail(email)).thenReturn(patient);
		when(visitRepository.findUpcomingByPatient(eq(1L), any(LocalDate.class), any(LocalTime.class), eq(pageable)))
			.thenReturn(page);
		when(visitMapper.toVisitResponseDto(visit)).thenReturn(responseDto);
		Page<VisitResponseDto> result = visitService.findAllForPatient(VisitType.UPCOMING, pageable);
		assertThat(result.getContent()).containsExactly(responseDto);
		verify(patientService).getPatientByEmail(email);
		verify(visitRepository).findUpcomingByPatient(any(), any(), any(), any());
	}

	@Test
	void shouldReturnPastVisitsForPatient() {
		String email = "patient@mail.com";
		Patient patient = new Patient();
		patient.setId(1L);
		Visit visit = new Visit();
		VisitResponseDto responseDto = mock(VisitResponseDto.class);
		Pageable pageable = mock(Pageable.class);
		Page<Visit> page = new PageImpl<>(List.of(visit));
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(authentication.getName()).thenReturn(email);
		when(patientService.getPatientByEmail(email)).thenReturn(patient);
		when(visitRepository.findPastByPatient(eq(1L), any(LocalDate.class), any(LocalTime.class), eq(pageable)))
			.thenReturn(page);
		when(visitMapper.toVisitResponseDto(visit)).thenReturn(responseDto);
		Page<VisitResponseDto> result = visitService.findAllForPatient(VisitType.PAST, pageable);
		assertThat(result.getContent()).containsExactly(responseDto);
		verify(patientService).getPatientByEmail(email);
		verify(visitRepository).findPastByPatient(any(), any(), any(), any());
	}

}
