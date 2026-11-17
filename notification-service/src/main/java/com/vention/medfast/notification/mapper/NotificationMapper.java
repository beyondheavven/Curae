package com.vention.medfast.notification.mapper;

import com.vention.medfast.notification.dto.NotificationDetailsDto;
import com.vention.medfast.notification.dto.NotificationDto;
import com.vention.medfast.notification.model.Notification;
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
