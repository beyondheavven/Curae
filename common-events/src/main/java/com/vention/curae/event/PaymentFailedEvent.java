package com.vention.curae.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentFailedEvent(Long appointmentId, Long patientId, String patientEmail, BigDecimal amount,
		String failureReason, Instant occurredAt) implements Serializable {
	public PaymentFailedEvent {
		if (occurredAt == null) {
			occurredAt = Instant.now();
		}
	}
}
