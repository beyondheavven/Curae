package com.vention.medfast.visit.mapper;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.Status;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.dto.VisitResponseDto;
import com.vention.medfast.visit.model.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class VisitMapperTest {

	private VisitMapper visitMapper;

	@BeforeEach
	void setUp() {
		visitMapper = Mappers.getMapper(VisitMapper.class);
	}

	@Test
	void shouldMapDtoToEntityWhenInputIsValid() {
		VisitCreateDto dto = new VisitCreateDto(LocalDate.of(2030, 10, 12), LocalTime.of(14, 30), 1L, 1L, 1L, 1L);

		Doctor doctor = new Doctor();
		doctor.setId(1L);
		Patient patient = new Patient();
		patient.setId(1L);
		ProvidedService service = new ProvidedService();
		service.setId(1L);
		Clinic clinic = new Clinic();
		clinic.setId(1L);

		Visit visit = visitMapper.toEntity(dto, doctor, patient, service, clinic);

		assertThat(visit).isNotNull();
		assertThat(visit.getId()).isNull();
		assertThat(visit.getAppointment()).isNotNull();

		assertThat(visit.getAppointment().getDate()).isEqualTo(dto.scheduledDate());
		assertThat(visit.getAppointment().getTime()).isEqualTo(dto.scheduledTime());
		assertThat(visit.getAppointment().getDoctor()).isEqualTo(doctor);
		assertThat(visit.getAppointment().getPatient()).isEqualTo(patient);
		assertThat(visit.getAppointment().getService()).isEqualTo(service);
		assertThat(visit.getClinic()).isEqualTo(clinic);
		assertThat(visit.getAppointment().getStatus()).isEqualTo(Status.SCHEDULED);
	}

	@Test
	void shouldReturnNullWhenDtoIsNull() {
		assertThat(visitMapper.toEntity(null, null, null, null, null)).isNull();
	}

	@Test
	void shouldMapToVisitResponseDtoWhenInputIsValid() {
		Address address = new Address();
		address.setCity("city");
		address.setStreet("street");
		address.setHouseNumber("20");

		Clinic clinic = new Clinic();
		clinic.setAddress(address);

		Doctor doctor = new Doctor();
		doctor.setClinics(Set.of(clinic));

		Patient patient = new Patient();
		ProvidedService service = new ProvidedService();

		Appointment appointment = new Appointment();
		appointment.setStatus(Status.SCHEDULED);
		appointment.setDate(LocalDate.now());
		appointment.setTime(LocalTime.now());
		appointment.setDoctor(doctor);
		appointment.setPatient(patient);
		appointment.setService(service);

		Visit visit = new Visit();
		visit.setClinic(clinic);
		visit.setAppointment(appointment);

		VisitResponseDto dto = visitMapper.toVisitResponseDto(visit);

		assertThat(dto).isNotNull();
		assertThat(dto.doctor()).isNotNull();
		assertThat(dto.date()).isEqualTo(appointment.getDate());
		assertThat(dto.time()).isEqualTo(appointment.getTime());
		assertThat(dto.address()).isNotNull();

		assertThat(dto.address().city()).isEqualTo(address.getCity());
		assertThat(dto.address().street()).isEqualTo(address.getStreet());
		assertThat(dto.address().houseNumber()).isEqualTo(address.getHouseNumber());
	}

	@Test
	void shouldReturnNullWhenVisitIsNull() {
		assertThat(visitMapper.toVisitResponseDto(null)).isNull();
	}

}
