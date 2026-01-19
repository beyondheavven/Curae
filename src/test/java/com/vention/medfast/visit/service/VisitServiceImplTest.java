package com.vention.medfast.visit.service;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.Status;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.clinic.service.ClinicService;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.service.ProvidedServiceService;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.dto.VisitResponseDto;
import com.vention.medfast.visit.mapper.VisitMapper;
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
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VisitServiceImplTest {

	@Mock
	private VisitMapper visitMapper;

	@Mock
	private DoctorService doctorService;

	@Mock
	private ProvidedServiceService providedServiceService;

	@Mock
	private VisitRepository visitRepository;

	@Mock
	private PatientService patientService;

	@Mock
	private ClinicService clinicService;

	@Mock
	private Authentication authentication;

	@Mock
	private SecurityContext securityContext;

	@InjectMocks
	private VisitServiceImpl visitService;

	@Test
	public void shouldCreateVisitWhenInputIsValid() {
		VisitCreateDto dto = new VisitCreateDto(LocalDate.of(2030, 10, 12), LocalTime.of(14, 30), 1L, 1L, 1L, 1L);

		Patient patient = new Patient();
		patient.setId(1L);

		Clinic clinic = new Clinic();
		clinic.setId(1L);

		Doctor doctor = new Doctor();
		doctor.setId(1L);
		doctor.setClinics(Set.of(clinic));

		ProvidedService providedService = new ProvidedService();
		providedService.setId(1L);

		Appointment appEntity = new Appointment();
		appEntity.setId(1L);
		appEntity.setDate(LocalDate.of(2030, 10, 12));
		appEntity.setTime(LocalTime.of(14, 30));
		appEntity.setStatus(Status.SCHEDULED);
		appEntity.setPatient(patient);
		appEntity.setDoctor(doctor);
		appEntity.setService(providedService);

		Visit entity = new Visit();
		entity.setId(1L);
		entity.setAppointment(appEntity);
		entity.setClinic(clinic);

		when(visitMapper.toEntity(dto, doctor, patient, providedService, clinic)).thenReturn(entity);
		when(providedServiceService.getProvidedServiceById(anyLong())).thenReturn(providedService);
		when(doctorService.findDoctorById(anyLong())).thenReturn(doctor);
		when(patientService.getById(anyLong())).thenReturn(patient);
		when(clinicService.findById(anyLong())).thenReturn(clinic);
		when(visitRepository.save(entity)).thenReturn(entity);

		long resultId = visitService.create(dto);

		assertEquals(1L, resultId);

		verify(visitMapper).toEntity(dto, doctor, patient, providedService, clinic);
		verify(providedServiceService).getProvidedServiceById(anyLong());
		verify(doctorService).findDoctorById(anyLong());
		verify(patientService).getById(anyLong());
		verify(visitRepository).save(entity);
	}

	@Test
	void shouldReturnUpcomingVisitsForPatient_WhenInputIsValid() {
		String email = "patient@example.com";
		VisitType type = VisitType.UPCOMING;

		Patient patient = new Patient();
		patient.setId(1L);

		Visit visit = new Visit();
		VisitResponseDto dto = mock(VisitResponseDto.class);
		Pageable pageable = mock(Pageable.class);
		Page<Visit> page = new PageImpl<>(List.of(visit), pageable, 1);

		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(authentication.getName()).thenReturn(email);
		when(patientService.getPatientByEmail(email)).thenReturn(patient);

		when(visitRepository.findUpcomingByPatient(any(Long.class), any(LocalDate.class), any(LocalTime.class),
				any(Pageable.class)))
			.thenReturn(page);
		when(visitMapper.toVisitResponseDto(visit)).thenReturn(dto);

		Page<VisitResponseDto> result = visitService.findAllForPatient(type, pageable);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().getFirst()).isSameAs(dto);

		verify(patientService).getPatientByEmail(email);
		verify(visitRepository).findUpcomingByPatient(any(Long.class), any(LocalDate.class), any(LocalTime.class),
				any(Pageable.class));
		verify(visitMapper).toVisitResponseDto(visit);
	}

	@Test
	void shouldReturnPastVisitsForPatient_whenInputIsValid() {
		String email = "patient@example.com";
		VisitType type = VisitType.PAST;

		Patient patient = new Patient();
		patient.setId(1L);

		Visit visit = new Visit();
		VisitResponseDto dto = mock(VisitResponseDto.class);
		Pageable pageable = mock(Pageable.class);
		Page<Visit> page = new PageImpl<>(List.of(visit), pageable, 1);

		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(authentication.getName()).thenReturn(email);
		when(patientService.getPatientByEmail(email)).thenReturn(patient);

		when(visitRepository.findPastByPatient(any(Long.class), any(LocalDate.class), any(LocalTime.class),
				any(Pageable.class)))
			.thenReturn(page);
		when(visitMapper.toVisitResponseDto(visit)).thenReturn(dto);

		Page<VisitResponseDto> result = visitService.findAllForPatient(type, pageable);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().getFirst()).isSameAs(dto);

		verify(patientService).getPatientByEmail(email);
		verify(visitRepository).findPastByPatient(any(Long.class), any(LocalDate.class), any(LocalTime.class),
				any(Pageable.class));
		verify(visitMapper).toVisitResponseDto(visit);
	}

}
