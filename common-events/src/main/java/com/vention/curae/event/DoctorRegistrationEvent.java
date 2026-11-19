package com.vention.curae.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

public record DoctorRegistrationEvent(Long userId, String email, String firstName, String lastName, String birthDate,
		String phoneNumber, String licenseNumber, Set<Long> specializationIds,
		Instant occurredAt) implements Serializable {

	public DoctorRegistrationEvent {
		if (occurredAt == null) {
			occurredAt = Instant.now();
		}
	}

}