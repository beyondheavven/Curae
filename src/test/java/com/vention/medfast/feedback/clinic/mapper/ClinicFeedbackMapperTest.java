package com.vention.medfast.feedback.clinic.mapper;

import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackCreateDto;
import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackDto;
import com.vention.medfast.feedback.clinic.model.ClinicFeedback;
import com.vention.medfast.patient.model.Patient;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ClinicFeedbackMapperTest {

	private final ClinicFeedbackMapper mapper = Mappers.getMapper(ClinicFeedbackMapper.class);

	@Test
	void shouldMapToDto_whenInputIsValid() {
		Clinic clinic = new Clinic();
		clinic.setId(1L);

		Patient patient = new Patient();
		patient.setId(5L);

		ClinicFeedback entity = new ClinicFeedback();
		entity.setId(1L);
		entity.setClinic(clinic);
		entity.setPatient(patient);
		entity.setRating(5);
		entity.setSummary("Excellent clinic");
		entity.setCreatedAt(LocalDateTime.now());

		ClinicFeedbackDto dto = mapper.toDto(entity);

		assertEquals(1L, dto.clinicId());
		assertEquals(5L, dto.patientId());
		assertEquals(5, dto.rating());
		assertEquals("Excellent clinic", dto.summary());
		assertNotNull(dto.createdAt());
	}

	@Test
	void shouldMapToEntity_whenInputIsValid() {
		Clinic clinic = new Clinic();
		clinic.setId(1L);

		Patient patient = new Patient();
		patient.setId(5L);

		ClinicFeedbackCreateDto createDto = new ClinicFeedbackCreateDto(5L, 4, "Great service");

		ClinicFeedback entity = mapper.toEntity(createDto, clinic, patient);

		assertNull(entity.getId());
		assertEquals(clinic, entity.getClinic());
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
