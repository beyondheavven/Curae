package com.vention.medfast.visit.service;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.service.AppointmentService;
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
import org.mockito.ArgumentCaptor;
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

	@InjectMocks
	private VisitServiceImpl visitService;

	@Test
    public void shouldCreateVisitWhenInputIsValid() {
        Long patientId = 1L;
        Long doctorId = 2L;
        Long serviceId = 1L;
        Long clinicId = 2L;

        VisitCreateDto dto = new VisitCreateDto(
            "Diagnosis", "Symptoms", "Report",
            doctorId, serviceId, clinicId
        );

        Patient patient = new Patient();
        patient.setId(patientId);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        ProvidedService service = new ProvidedService();
        service.setId(serviceId);

        Clinic clinic = new Clinic();
        clinic.setId(clinicId);

        when(patientService.getById(patientId)).thenReturn(patient);
        when(doctorService.findDoctorById(doctorId)).thenReturn(doctor);
        when(providedServiceService.getProvidedServiceById(serviceId)).thenReturn(service);
        when(clinicService.findById(clinicId)).thenReturn(clinic);

        when(visitRepository.save(any(Visit.class))).thenAnswer(inv -> {
            Visit v = inv.getArgument(0);
            v.setId(100L);
            return v;
        });

        long result = visitService.create(patientId, dto);

        assertThat(result).isEqualTo(100L);
        ArgumentCaptor<Visit> visitCaptor = ArgumentCaptor.forClass(Visit.class);
        verify(visitRepository).save(visitCaptor.capture());
        Visit capturedVisit = visitCaptor.getValue();

        assertThat(capturedVisit.getDiagnosis()).isEqualTo("Diagnosis");
        assertThat(capturedVisit.getSymptoms()).isEqualTo("Symptoms");
        assertThat(capturedVisit.getDoctor()).isEqualTo(doctor);
        assertThat(capturedVisit.getProvidedService()).isEqualTo(service);
        assertThat(capturedVisit.getClinic()).isEqualTo(clinic);
        assertThat(capturedVisit.getPatient()).isEqualTo(patient);

        assertThat(capturedVisit.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        assertThat(capturedVisit.getType()).isEqualTo(AppointmentType.VISIT);
        assertThat(capturedVisit.getDate()).isEqualTo(LocalDate.now());
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
