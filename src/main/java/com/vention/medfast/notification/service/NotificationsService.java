package com.vention.medfast.notification.service;

import com.vention.medfast.notification.dto.NotificationDetailsDto;
import com.vention.medfast.notification.dto.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationsService {

	Page<NotificationDto> getPatientNotifications(Long userId, Pageable pageable);

	NotificationDetailsDto getPatientNotificationDetails(Long notificationId);

	void markAsRead(Long notificationId, Long userId);

	boolean isOwner(Long notificationId, Long patientId);

}
