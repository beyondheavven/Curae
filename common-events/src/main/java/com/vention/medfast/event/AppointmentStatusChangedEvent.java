package com.vention.medfast.event;

import java.io.Serializable;
import java.time.Instant;

public record AppointmentStatusChangedEvent(Long appointmentId, Long patientId, Long doctorId, String oldStatus,
		String newStatus, Instant occurredAt) implements Serializable {

	public AppointmentStatusChangedEvent {
		if (occurredAt == null) {
			occurredAt = Instant.now();
		}
	}
}
