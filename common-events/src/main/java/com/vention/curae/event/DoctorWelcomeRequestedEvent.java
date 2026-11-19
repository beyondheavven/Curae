package com.vention.curae.event;

import java.io.Serializable;
import java.time.Instant;

public record DoctorWelcomeRequestedEvent(String email, String temporaryPassword, String token,
		Instant occurredAt) implements Serializable {

	public DoctorWelcomeRequestedEvent {
		if (occurredAt == null) {
			occurredAt = Instant.now();
		}
	}

}
