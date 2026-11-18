package com.vention.medfast.registration.event;

import com.vention.medfast.registration.dto.AddressDto;
import com.vention.medfast.registration.dto.PersonDto;

public record PatientRegisteredEvent(Long userId, PersonDto person, AddressDto address) {
}
