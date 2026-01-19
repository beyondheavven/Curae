package com.vention.medfast.visit.mapper;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.Status;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.model.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class VisitMapperTest {

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
        appointment.setStatus(Status.SCHEDULED_CONFIRMED);
        appointment.setType(AppointmentType.VISIT);

		Visit visit = visitMapper.toEntity(dto, appointment);

		assertThat(visit).isNotNull();
		assertThat(visit.getId()).isNull();
		assertThat(visit.getAppointment()).isNotNull();

        assertThat(visit.getAppointment()).isEqualTo(appointment);
        assertThat(visit.getDiagnosis()).isEqualTo(dto.diagnosis());
        assertThat(visit.getSymptoms()).isEqualTo(dto.symptoms());
        assertThat(visit.getMedicalReport()).isEqualTo(dto.medicalReport());
	}

	@Test
	void shouldReturnNullWhenDtoIsNull() {
		assertThat(visitMapper.toEntity(null, null)).isNull();
	}

}
