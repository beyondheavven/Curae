package com.vention.curae.notification.dto;

import com.vention.curae.notification.model.NotificationStatus;
import com.vention.curae.notification.model.NotificationType;
import java.time.Instant;

public record NotificationDto(Long id, Long appointmentId, NotificationType type, NotificationStatus status,
		Instant createdAt) {
}
