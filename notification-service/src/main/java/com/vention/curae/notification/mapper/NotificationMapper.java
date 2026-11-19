package com.vention.curae.notification.mapper;

import com.vention.curae.notification.dto.NotificationDetailsDto;
import com.vention.curae.notification.dto.NotificationDto;
import com.vention.curae.notification.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

	public NotificationDto toDto(Notification notification) {
		return new NotificationDto(notification.getId(), notification.getAppointmentId(), notification.getType(),
				notification.getStatus(), notification.getCreatedAt());
	}

	public NotificationDetailsDto toDetailsDto(Notification notification) {
		return new NotificationDetailsDto(notification.getId(), notification.getAppointmentId(), notification.getType(),
				notification.getStatus(), notification.getCreatedAt());
	}

}
