package com.vention.medfast.event;

import java.io.Serializable;
import java.time.Instant;

public record UserProfileIdentitySyncRequestedEvent(Long userId, String email, String firstName, String lastName,
		String birthDate, String profileUrl, boolean emailChanged, Instant occurredAt) implements Serializable {

	public UserProfileIdentitySyncRequestedEvent {
		if (occurredAt == null) {
			occurredAt = Instant.now();
		}
	}

}
