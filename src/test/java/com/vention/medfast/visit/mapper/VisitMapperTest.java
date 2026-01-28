package com.vention.medfast.visit.mapper;

import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.mapper.VisitMapper;
import com.vention.medfast.appointment.model.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class VisitMapperTest {

	private VisitMapper visitMapper;

	@BeforeEach
	void setUp() {
		visitMapper = Mappers.getMapper(VisitMapper.class);
	}

	@Test
	void shouldMapDtoToEntityWhenInputIsValid() {
		VisitCreateDto dto = new VisitCreateDto("Diagnosis", "Symptoms", "Medical report", 1L, 2L, 1L);

		Visit visit = visitMapper.toEntity(dto);

		assertThat(visit).isNotNull();
		assertThat(visit.getId()).isNull();
		assertThat(visit.getDiagnosis()).isEqualTo("Diagnosis");
		assertThat(visit.getSymptoms()).isEqualTo("Symptoms");
		assertThat(visit.getMedicalReport()).isEqualTo("Medical report");

		assertThat(visit.getClinic()).isNull();
	}

}
