package com.vention.medfast.notification.mapper;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.notification.dto.NotificationDetailsDto;
import com.vention.medfast.notification.dto.NotificationDto;
import com.vention.medfast.notification.model.Notification;
import com.vention.medfast.notification.model.NotificationStatus;
import com.vention.medfast.notification.model.NotificationType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationMapperTest {

	private final NotificationMapper mapper = Mappers.getMapper(NotificationMapper.class);

	@Test
	void toDtoMapsFieldsIncludingAppointmentId() {
		Notification source = buildNotification();

		NotificationDto dto = mapper.toDto(source);

		assertThat(dto.id()).isEqualTo(10L);
		assertThat(dto.appointmentId()).isEqualTo(20L);
		assertThat(dto.type()).isEqualTo(NotificationType.LAB_RESULT_READY);
		assertThat(dto.status()).isEqualTo(NotificationStatus.UNREAD);
		assertThat(dto.createdAt()).isEqualTo(source.getCreatedAt());
	}

	@Test
	void toDetailsDtoMapsFieldsIncludingAppointmentId() {
		Notification source = buildNotification();

		NotificationDetailsDto dto = mapper.toDetailsDto(source);

		assertThat(dto.id()).isEqualTo(10L);
		assertThat(dto.appointmentId()).isEqualTo(20L);
		assertThat(dto.type()).isEqualTo(NotificationType.LAB_RESULT_READY);
		assertThat(dto.status()).isEqualTo(NotificationStatus.UNREAD);
		assertThat(dto.createdAt()).isEqualTo(source.getCreatedAt());
	}

	private Notification buildNotification() {
		Notification notification = new Notification();
		notification.setId(10L);
		notification.setType(NotificationType.LAB_RESULT_READY);
		notification.setStatus(NotificationStatus.UNREAD);
		notification.setCreatedAt(Instant.parse("2024-01-01T10:00:00Z"));

		Appointment appointment = new Appointment();
		appointment.setId(20L);
		notification.setAppointment(appointment);

		return notification;
	}

}
