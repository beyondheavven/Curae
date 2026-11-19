package com.vention.curae.registration.event;

import com.vention.curae.registration.dto.AddressDto;
import com.vention.curae.registration.dto.PersonDto;

public record PatientRegisteredEvent(Long userId, PersonDto person, AddressDto address) {
}
