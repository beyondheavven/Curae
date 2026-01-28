package com.vention.medfast.appointment.onlineConsultation.mapper;

import com.vention.medfast.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;
import com.vention.medfast.appointment.mapper.OnlineConsultationMapper;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.OnlineConsultation;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class OnlineConsultationMapperTest {

	private final OnlineConsultationMapper mapper = Mappers.getMapper(OnlineConsultationMapper.class);

	@Test
	void toDtoMapsAllFields() {
		Patient patient = new Patient();
		patient.setId(2L);
		Doctor doctor = new Doctor();
		doctor.setId(3L);

		OnlineConsultation oc = new OnlineConsultation();
		oc.setId(99L);
		oc.setPatient(patient);
		oc.setDoctor(doctor);
		oc.setMeetingLink("meet-link");

		oc.setDate(LocalDate.of(2026, 2, 1));
		oc.setTime(LocalTime.of(10, 15));
		oc.setStatus(AppointmentStatus.SCHEDULED);
		oc.setType(AppointmentType.ONLINE);
		oc.setUpdatedAt(LocalDateTime.of(2026, 1, 1, 12, 0));

		OnlineConsultationDetails dto = mapper.toDto(oc);

		assertThat(dto.id()).isEqualTo(99L);
		assertThat(dto.patientId()).isEqualTo(2L);
		assertThat(dto.doctorId()).isEqualTo(3L);
		assertThat(dto.date()).isEqualTo(LocalDate.of(2026, 2, 1));
		assertThat(dto.time()).isEqualTo(LocalTime.of(10, 15));
		assertThat(dto.status()).isEqualTo(AppointmentStatus.SCHEDULED);
		assertThat(dto.type()).isEqualTo(AppointmentType.ONLINE);
		assertThat(dto.updatedAt()).isEqualTo(LocalDateTime.of(2026, 1, 1, 12, 0));
		assertThat(dto.meetingLink()).isEqualTo("meet-link");
	}

	@Test
	void toEntityPopulatesAppointmentAndMeetingLink() {
		CreateOnlineConsultationRequest req = new CreateOnlineConsultationRequest(1L, 2L, LocalDate.of(2026, 3, 1),
				LocalTime.of(9, 0), AppointmentStatus.SCHEDULED_CONFIRMED, "meet");

		OnlineConsultation oc = mapper.toEntity(req);

		assertThat(oc.getMeetingLink()).isEqualTo("meet");
		assertThat(oc.getDate()).isEqualTo(LocalDate.of(2026, 3, 1));
		assertThat(oc.getTime()).isEqualTo(LocalTime.of(9, 0));
		assertThat(oc.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED_CONFIRMED);
		assertThat(oc.getType()).isEqualTo(AppointmentType.ONLINE);
		assertThat(oc.getCreatedAt()).isNull();
		assertThat(oc.getUpdatedAt()).isNull();
	}

	@Test
	void updateAppointmentSetsFieldsAndUpdatedAt() {
		UpdateOnlineConsultationRequest req = new UpdateOnlineConsultationRequest(5L, 1L, 2L, LocalDate.of(2026, 4, 10),
				LocalTime.of(14, 30), AppointmentStatus.IN_CONSULTATION, "new-link");

		Appointment appt = new Appointment();
		appt.setStatus(AppointmentStatus.SCHEDULED);
		appt.setType(AppointmentType.ONLINE);

		mapper.updateAppointment(req, appt);

		assertThat(appt.getDate()).isEqualTo(LocalDate.of(2026, 4, 10));
		assertThat(appt.getTime()).isEqualTo(LocalTime.of(14, 30));
		assertThat(appt.getStatus()).isEqualTo(AppointmentStatus.IN_CONSULTATION);
		assertThat(appt.getType()).isEqualTo(AppointmentType.ONLINE);
		assertThat(appt.getUpdatedAt()).isNotNull();
	}

	@Test
	void updateOnlineConsultationUpdatesMeetingLink() {
		UpdateOnlineConsultationRequest req = new UpdateOnlineConsultationRequest(5L, 1L, 2L, LocalDate.now(),
				LocalTime.NOON, AppointmentStatus.COMPLETED, "updated");
		OnlineConsultation oc = new OnlineConsultation();
		oc.setMeetingLink("old");

		mapper.updateOnlineConsultation(req, oc);

		assertThat(oc.getMeetingLink()).isEqualTo("updated");
	}

}
