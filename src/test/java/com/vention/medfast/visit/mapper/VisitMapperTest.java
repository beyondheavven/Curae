package com.vention.medfast.visit.mapper;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.model.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class VisitMapperTest {

	private VisitMapper visitMapper;

	@BeforeEach
	void setUp() {
		visitMapper = Mappers.getMapper(VisitMapper.class);
	}

	@Test
	void shouldMapDtoToEntityWhenInputIsValid() {
		VisitCreateDto dto = new VisitCreateDto(1L, "Diagnosis", "Symptoms", "Medical report");

		Appointment appointment = new Appointment();
		appointment.setId(1L);
		appointment.setDate(LocalDate.of(2030, 10, 12));
		appointment.setTime(LocalTime.of(14, 30));
		appointment.setStatus(AppointmentStatus.SCHEDULED_CONFIRMED);

		Visit visit = visitMapper.toEntity(dto, appointment);

		assertThat(visit).isNotNull();
		assertThat(visit.getId()).isNull();
		assertThat(visit.getAppointment()).isEqualTo(appointment);
		assertThat(visit.getDiagnosis()).isEqualTo("Diagnosis");
		assertThat(visit.getSymptoms()).isEqualTo("Symptoms");
		assertThat(visit.getMedicalReport()).isEqualTo("Medical report");

		assertThat(visit.getClinic()).isNull();
	}

}
