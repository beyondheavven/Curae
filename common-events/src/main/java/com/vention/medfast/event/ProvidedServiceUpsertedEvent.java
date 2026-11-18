package com.vention.medfast.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public record ProvidedServiceUpsertedEvent(Long serviceId, String name, String description, BigDecimal price,
		Integer durationMinutes, Instant occurredAt) implements Serializable {

	public ProvidedServiceUpsertedEvent {
		if (occurredAt == null) {
			occurredAt = Instant.now();
		}
	}

}
