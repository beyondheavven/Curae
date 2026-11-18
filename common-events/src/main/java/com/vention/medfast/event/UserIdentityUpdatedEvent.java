package com.vention.medfast.event;

import java.io.Serializable;
import java.time.Instant;

public record UserIdentityUpdatedEvent(Long userId, String email, String firstName, String lastName, String birthDate,
		String status, Instant occurredAt) implements Serializable {

	public UserIdentityUpdatedEvent {
		if (occurredAt == null) {
			occurredAt = Instant.now();
		}
	}

}