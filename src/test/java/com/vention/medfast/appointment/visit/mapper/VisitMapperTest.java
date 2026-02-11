package com.vention.medfast.appointment.visit.mapper;

import com.vention.medfast.appointment.dto.DoctorForVisitResponseDto;
import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.mapper.VisitMapper;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.Visit;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VisitMapperTest {

	private VisitMapper visitMapper;

	@BeforeEach
	void setUp() {
		visitMapper = Mappers.getMapper(VisitMapper.class);
	}

	@Test
	@DisplayName("Should map DTO and entities to Visit with default values")
	void shouldMapDtoToEntityWhenInputIsValid() {
		VisitCreateDto dto = new VisitCreateDto("Diagnosis", "Symptoms", "Medical report", 1L, 2L, 3L);

		Patient patient = new Patient();
		patient.setId(10L);

		Doctor doctor = new Doctor();
		doctor.setId(20L);

		ProvidedService service = new ProvidedService();
		service.setId(30L);

		Clinic clinic = new Clinic();
		clinic.setId(40L);

		Visit visit = visitMapper.toEntity(dto, patient, doctor, service, clinic);

		assertThat(visit).isNotNull();
		assertThat(visit.getId()).isNull();
		assertThat(visit.getDiagnosis()).isEqualTo("Diagnosis");
		assertThat(visit.getSymptoms()).isEqualTo("Symptoms");
		assertThat(visit.getMedicalReport()).isEqualTo("Medical report");
		assertThat(visit.getPatient()).isEqualTo(patient);
		assertThat(visit.getDoctor()).isEqualTo(doctor);
		assertThat(visit.getProvidedService()).isEqualTo(service);
		assertThat(visit.getClinic()).isEqualTo(clinic);
		assertThat(visit.getDate()).isNotNull();
		assertThat(visit.getTime()).isNotNull();
		assertThat(visit.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
		assertThat(visit.getType()).isEqualTo(AppointmentType.VISIT);
	}

	@Test
	void shouldMapSpecializationToName() {
		Specialization specialization = new Specialization();
		specialization.setName("Cardiology");

		String result = visitMapper.map(specialization);

		assertThat(result).isEqualTo("Cardiology");
	}

	@Test
	void shouldMapDoctorToDoctorForVisitResponseDto() {
		Doctor doctor = new Doctor();

		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		doctor.setUser(user);

		Specialization specialization = new Specialization();
		specialization.setName("Cardiology");
		doctor.setSpecializations(Set.of(specialization));

		DoctorForVisitResponseDto dto = visitMapper.toDoctorForVisitDto(doctor);

		assertThat(dto).isNotNull();
		assertThat(dto.firstName()).isEqualTo("John");
		assertThat(dto.lastName()).isEqualTo("Doe");
		assertThat(dto.specializations()).contains("Cardiology");
	}

}
