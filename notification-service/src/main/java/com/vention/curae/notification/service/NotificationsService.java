package com.vention.curae.notification.service;

import com.vention.curae.notification.dto.NotificationDetailsDto;
import com.vention.curae.notification.dto.NotificationDto;
import com.vention.curae.notification.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationsService {

	Page<NotificationDto> getPatientNotifications(Long patientId, Pageable pageable);

	NotificationDetailsDto getNotificationDetails(Long notificationId);

	void markAsRead(Long notificationId, Long patientId);

	boolean isOwner(Long notificationId, Long patientId);

	void create(Long patientId, Long appointmentId, NotificationType type);

}
