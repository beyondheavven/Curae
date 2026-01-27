package com.vention.medfast.notification.eventListener.eventModel;

import com.vention.medfast.notification.model.NotificationType;

public record StatusChangedEvent(NotificationType type, Long appointmentId, Long patientId) {
}
