package com.vention.medfast.appointment.test.mapper;

import com.vention.medfast.appointment.dto.TestCreateWithLabsDto;
import com.vention.medfast.appointment.mapper.TestMapper;
import com.vention.medfast.appointment.model.Test;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.patient.model.Patient;
import org.junit.jupiter.api.DisplayName;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TestMapperTest {

	private final TestMapper mapper = Mappers.getMapper(TestMapper.class);

	@org.junit.jupiter.api.Test
	@DisplayName("Should map DTO, Patient and Clinic to Test entity correctly")
	void shouldMapDtoToEntity() {
		LocalDate date = LocalDate.of(2026, 5, 20);
		LocalTime time = LocalTime.of(14, 30);

		TestCreateWithLabsDto dto = new TestCreateWithLabsDto(1L, 2L, date, time, List.of(100L, 101L));

		Patient patient = new Patient();
		patient.setId(1L);
		patient.setCitizenship("USA");

		Clinic clinic = new Clinic();
		clinic.setId(2L);
		clinic.setName("Central Hospital");

		Test result = mapper.toLabAppointment(dto, patient, clinic);

		assertThat(result).isNotNull();

		assertThat(result.getDate()).isEqualTo(date);
		assertThat(result.getTime()).isEqualTo(time);

		assertThat(result.getPatient()).isEqualTo(patient);
		assertThat(result.getClinic()).isEqualTo(clinic);

		assertThat(result.getId()).isNull();
		assertThat(result.getStatus()).isNull();
		assertThat(result.getType()).isNull();
		assertThat(result.getReferral()).isNull();
		assertThat(result.getCreatedAt()).isNull();
		assertThat(result.getUpdatedAt()).isNull();
	}

}
