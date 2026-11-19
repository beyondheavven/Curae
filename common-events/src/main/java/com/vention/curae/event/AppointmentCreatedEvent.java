package com.vention.curae.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record AppointmentCreatedEvent(Long appointmentId, Long patientId, String patientEmail, String patientName,
		Long doctorId, String doctorName, LocalDateTime scheduledTime, BigDecimal price,
		Instant occurredAt) implements Serializable {
	public AppointmentCreatedEvent {
		if (occurredAt == null) {
			occurredAt = Instant.now();
		}
	}
}
