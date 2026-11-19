package com.vention.curae.event;

import java.io.Serializable;
import java.time.Instant;

public record PatientRegistrationEvent(Long userId, String email, String firstName, String lastName, String birthDate,
		String gender, String citizenship, AddressData address, Instant occurredAt) implements Serializable {

	public PatientRegistrationEvent {
		if (occurredAt == null) {
			occurredAt = Instant.now();
		}
	}

	public record AddressData(String street, String city, String apartment, String houseNumber, String state,
			String zipCode, String phoneNumber) implements Serializable {
	}

}