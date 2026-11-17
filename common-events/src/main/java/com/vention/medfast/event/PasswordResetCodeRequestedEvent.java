package com.vention.medfast.event;

import java.io.Serializable;
import java.time.Instant;

public record PasswordResetCodeRequestedEvent(String email, String code, Instant occurredAt) implements Serializable {

	public PasswordResetCodeRequestedEvent {
		if (occurredAt == null) {
			occurredAt = Instant.now();
		}
	}

}
