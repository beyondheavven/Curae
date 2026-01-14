package com.vention.medfast.visit.service;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.Status;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.service.ProvidedServiceService;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.mapper.VisitMapper;
import com.vention.medfast.visit.model.Visit;
import com.vention.medfast.visit.repository.VisitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
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

	@InjectMocks
	private VisitServiceImpl visitService;

	@Test
	public void shouldCreateVisitWhenInputIsValid() {
		VisitCreateDto dto = new VisitCreateDto(LocalDate.of(2030, 10, 12), LocalTime.of(14, 30), 1L, 1L, 1L);

		Patient patient = new Patient();
		patient.setId(1L);

		Doctor doctor = new Doctor();
		doctor.setId(1L);

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

		when(visitMapper.toEntity(dto, doctor, patient, providedService)).thenReturn(entity);
		when(providedServiceService.getProvidedServiceById(anyLong())).thenReturn(providedService);
		when(doctorService.findDoctorById(anyLong())).thenReturn(doctor);
		when(patientService.getById(anyLong())).thenReturn(patient);
		when(visitRepository.save(entity)).thenReturn(entity);

		long resultId = visitService.create(dto);

		assertEquals(1L, resultId);

		verify(visitMapper).toEntity(dto, doctor, patient, providedService);
		verify(providedServiceService).getProvidedServiceById(anyLong());
		verify(doctorService).findDoctorById(anyLong());
		verify(patientService).getById(anyLong());
		verify(visitRepository).save(entity);
	}

}
