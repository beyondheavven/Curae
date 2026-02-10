package com.vention.medfast.appointment.visit.service;

import com.vention.medfast.appointment.dto.VisitListItemDto;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.service.VisitServiceImpl;
import com.vention.medfast.appointment.service.cache.VisitCacheService;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.clinic.service.ClinicService;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.service.ProvidedServiceService;
import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.mapper.VisitMapper;
import com.vention.medfast.appointment.model.Visit;
import com.vention.medfast.appointment.model.VisitType;
import com.vention.medfast.appointment.repository.VisitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
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
	private PatientService patientService;

	@Mock
	private DoctorService doctorService;

	@Mock
	private ProvidedServiceService providedServiceService;

	@Mock
	private ClinicService clinicService;

	@Mock
	private Authentication authentication;

	@Mock
	private SecurityContext securityContext;

	@Mock
	private VisitCacheService visitCacheService;

	@InjectMocks
	private VisitServiceImpl visitService;

	@Test
	public void shouldCreateVisitWhenInputIsValid() {
		Long patientId = 1L;
		Long doctorId = 2L;
		Long serviceId = 1L;
		Long clinicId = 2L;

		VisitCreateDto dto = new VisitCreateDto("Diagnosis", "Symptoms", "Report", doctorId, serviceId, clinicId);

		Patient patient = new Patient();
		patient.setId(patientId);

		Doctor doctor = new Doctor();
		doctor.setId(doctorId);

		ProvidedService service = new ProvidedService();
		service.setId(serviceId);

		Clinic clinic = new Clinic();
		clinic.setId(clinicId);

		Visit mappedVisit = new Visit();
		mappedVisit.setDiagnosis("Diagnosis");
		mappedVisit.setPatient(patient);
		mappedVisit.setStatus(AppointmentStatus.COMPLETED);
		mappedVisit.setType(AppointmentType.VISIT);

		when(patientService.getById(patientId)).thenReturn(patient);
		when(doctorService.findDoctorById(doctorId)).thenReturn(doctor);
		when(providedServiceService.getProvidedServiceById(serviceId)).thenReturn(service);
		when(clinicService.findById(clinicId)).thenReturn(clinic);

		when(visitMapper.toEntity(dto, patient, doctor, service, clinic)).thenReturn(mappedVisit);

		when(visitRepository.save(mappedVisit)).thenAnswer(inv -> {
			Visit v = inv.getArgument(0);
			v.setId(100L);
			return v;
		});

		long result = visitService.create(patientId, dto);
		assertThat(result).isEqualTo(100L);
		verify(visitMapper).toEntity(dto, patient, doctor, service, clinic);
		verify(visitRepository).save(mappedVisit);
	}

	@Test
	void shouldReturnUpcomingVisitsForPatient() {
		String email = "patient@mail.com";
		Patient patient = new Patient();
		patient.setId(1L);
		VisitListItemDto dto = new VisitListItemDto("John", "Doe", "Clinic", "Kyiv", LocalDate.now(), LocalTime.NOON,
				AppointmentStatus.COMPLETED);
		Slice<VisitListItemDto> slice = new SliceImpl<>(List.of(dto));
		Pageable pageable = mock(Pageable.class);

		SecurityContextHolder.setContext(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn(email);
		when(patientService.getPatientByEmail(email)).thenReturn(patient);

		when(visitCacheService.getVisits(eq(1L), eq(VisitType.UPCOMING), anyInt(), anyInt())).thenReturn(slice);

		Slice<VisitListItemDto> result = visitService.findAllForPatient(VisitType.UPCOMING, pageable);

		assertThat(result.getContent()).containsExactly(dto);
		verify(visitCacheService).getVisits(any(), any(), anyInt(), anyInt());
	}

	@Test
	void shouldReturnPastVisitsForPatient() {
		String email = "patient@mail.com";
		Patient patient = new Patient();
		patient.setId(1L);
		VisitListItemDto dto = new VisitListItemDto("Jane", "Doe", "Clinic", "Lviv", LocalDate.now(), LocalTime.NOON,
				AppointmentStatus.COMPLETED);
		Slice<VisitListItemDto> slice = new SliceImpl<>(List.of(dto));
		Pageable pageable = mock(Pageable.class);

		SecurityContextHolder.setContext(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn(email);
		when(patientService.getPatientByEmail(email)).thenReturn(patient);

		when(visitCacheService.getVisits(eq(1L), eq(VisitType.PAST), anyInt(), anyInt())).thenReturn(slice);

		Slice<VisitListItemDto> result = visitService.findAllForPatient(VisitType.PAST, pageable);

		assertThat(result.getContent()).containsExactly(dto);
		verify(visitCacheService).getVisits(any(), any(), anyInt(), anyInt());
	}

}
