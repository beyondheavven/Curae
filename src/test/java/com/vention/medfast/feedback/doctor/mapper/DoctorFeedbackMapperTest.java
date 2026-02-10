package com.vention.medfast.feedback.doctor.mapper;

import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackCreateDto;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackDto;
import com.vention.medfast.feedback.doctor.model.DoctorFeedback;
import com.vention.medfast.patient.model.Patient;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DoctorFeedbackMapperTest {

	private final DoctorFeedbackMapper mapper = Mappers.getMapper(DoctorFeedbackMapper.class);

	@Test
	void shouldMapToDto_whenInputIsValid() {
		Doctor doctor = new Doctor();
		doctor.setId(10L);

		Patient patient = new Patient();
		patient.setId(5L);

		DoctorFeedback entity = new DoctorFeedback();
		entity.setId(1L);
		entity.setDoctor(doctor);
		entity.setPatient(patient);
		entity.setRating(5);
		entity.setSummary("Excellent doctor");
		entity.setCreatedAt(LocalDateTime.now());

		DoctorFeedbackDto dto = mapper.toDto(entity);

		assertEquals(10L, dto.doctorId());
		assertEquals(5L, dto.patientId());
		assertEquals(5, dto.rating());
		assertEquals("Excellent doctor", dto.summary());
		assertNotNull(dto.createdAt());
	}

	@Test
	void shouldMapToEntity_whenInputIsValid() {
		Doctor doctor = new Doctor();
		doctor.setId(10L);

		Patient patient = new Patient();
		patient.setId(5L);

		DoctorFeedbackCreateDto createDto = new DoctorFeedbackCreateDto(5L, 4, "Great service");

		DoctorFeedback entity = mapper.toEntity(createDto, doctor, patient);

		assertNull(entity.getId());
		assertEquals(doctor, entity.getDoctor());
		assertEquals(patient, entity.getPatient());
		assertEquals(4, entity.getRating());
		assertEquals("Great service", entity.getSummary());
		assertNull(entity.getCreatedAt());
	}

	@Test
	void shouldReturnNull_whenEntityIsNull() {
		assertNull(mapper.toDto(null));
	}

	@Test
	void shouldReturnNull_whenDtoIsNull() {
		assertNull(mapper.toEntity(null, null, null));
	}

}
