package com.vention.medfast.appointment.mapper;

import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.dto.AppointmentUpdateDto;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.referral.model.Referral;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentMapperTest {

	private AppointmentMapper mapper;

	@BeforeEach
	void setUp() {
		mapper = Mappers.getMapper(AppointmentMapper.class);
	}

	@Test
	void toAppointmentSetsReferralAndIgnoresTypeAndStatus() {
		AppointmentCreateDto dto = new AppointmentCreateDto(11L, LocalDate.now().plusDays(3), LocalTime.of(15, 30));
		Patient patient = new Patient();
		patient.setId(7L);
		Referral referral = new Referral();
		referral.setId(5L);

		Appointment appointment = mapper.toAppointment(dto, patient, referral);

		assertThat(appointment.getReferral()).isSameAs(referral);
		assertThat(appointment.getPatient()).isSameAs(patient);
		assertThat(appointment.getType()).isNull();
		assertThat(appointment.getStatus()).isNull();
		assertThat(appointment.getId()).isNull();
		assertThat(appointment.getDate()).isEqualTo(dto.date());
		assertThat(appointment.getTime()).isEqualTo(dto.time());
	}

	@Test
	void toLabAppointmentLeavesReferralNullAndTypeUnset() {
		AppointmentCreateWithLabsDto dto = new AppointmentCreateWithLabsDto(11L, LocalDate.now().plusDays(2),
				LocalTime.of(13, 45), List.of(1L, 2L, 3L));
		Doctor doctor = new Doctor();
		doctor.setId(1L);
		Patient patient = new Patient();
		patient.setId(2L);

		Appointment appointment = mapper.toLabAppointment(dto, patient);

		assertThat(appointment.getReferral()).isNull();
		assertThat(appointment.getType()).isNull();
		assertThat(appointment.getPatient()).isSameAs(patient);
		assertThat(appointment.getDate()).isEqualTo(dto.date());
		assertThat(appointment.getTime()).isEqualTo(dto.time());
		assertThat(appointment.getStatus()).isNull();
	}

	@Test
	void shouldUpdateAppointmentFromDto_whenInputIsValid() {
		Appointment appointment = new Appointment();
		appointment.setStatus(AppointmentStatus.SCHEDULED);
		appointment.setDate(LocalDate.of(2026, 10, 1));
		appointment.setTime(LocalTime.of(13, 45));

		AppointmentUpdateDto dto = new AppointmentUpdateDto(LocalDate.of(2026, 1, 18), LocalTime.of(10, 45),
				AppointmentStatus.COMPLETED);

		mapper.updateFromDto(dto, appointment);

		assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
		assertThat(appointment.getDate()).isEqualTo(LocalDate.of(2026, 1, 18));
		assertThat(appointment.getTime()).isEqualTo(LocalTime.of(10, 45));
	}

}
