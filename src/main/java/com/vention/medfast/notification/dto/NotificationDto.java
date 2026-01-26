package com.vention.medfast.notification.dto;

import com.vention.medfast.notification.model.NotificationStatus;
import com.vention.medfast.notification.model.NotificationType;
import java.time.Instant;

public record NotificationDto(Long id, Long appointmentId, NotificationType type, NotificationStatus status,
		Instant createdAt) {
}
