package com.vention.medfast.event;

import java.io.Serializable;
import java.time.Instant;

public record ProvidedServiceDeletedEvent(Long serviceId, Instant occurredAt) implements Serializable {

	public ProvidedServiceDeletedEvent {
		if (occurredAt == null) {
			occurredAt = Instant.now();
		}
	}

}
