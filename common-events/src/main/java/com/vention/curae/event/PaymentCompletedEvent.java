package com.vention.curae.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentCompletedEvent(Long paymentId, Long appointmentId, Long patientId, String patientEmail,
		BigDecimal amount, String currency, String stripePaymentIntentId, Instant occurredAt) implements Serializable {
	public PaymentCompletedEvent {
		if (occurredAt == null) {
			occurredAt = Instant.now();
		}
	}
}
