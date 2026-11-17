package com.vention.medfast.notification.service;

import com.vention.medfast.notification.dto.NotificationDetailsDto;
import com.vention.medfast.notification.dto.NotificationDto;
import com.vention.medfast.notification.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationsService {

	Page<NotificationDto> getPatientNotifications(Long patientId, Pageable pageable);

	NotificationDetailsDto getNotificationDetails(Long notificationId);

	void markAsRead(Long notificationId, Long patientId);

	boolean isOwner(Long notificationId, Long patientId);

	void create(Long patientId, Long appointmentId, NotificationType type);

}
