package com.vention.medfast.appointment.mapper;

import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.model.Status;
import com.vention.medfast.patient.model.Patient;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentMapperTest {

	private AppointmentMapper appointmentMapper;

	@BeforeEach
	void setUp() {
		appointmentMapper = Mappers.getMapper(AppointmentMapper.class);
	}

	@Test
	void shouldMapCreateRequestToEntity() {
		AppointmentCreateWithLabsDto dto = new AppointmentCreateWithLabsDto(5L, LocalDate.of(2030, 1, 1),
				LocalTime.of(9, 30), java.util.List.of(1L, 2L));

		Patient patient = new Patient();
		patient.setId(5L);

		var appointment = appointmentMapper.toEntity(dto, patient);

		assertThat(appointment).isNotNull();
		assertThat(appointment.getPatient()).isEqualTo(patient);
		assertThat(appointment.getDate()).isEqualTo(dto.date());
		assertThat(appointment.getTime()).isEqualTo(dto.time());
		assertThat(appointment.getStatus()).isEqualTo(Status.SCHEDULED);
	}

}
