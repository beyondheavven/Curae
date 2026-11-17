package com.vention.medfast.auth.registration.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatientRegistrationRequestDto {

	@Valid
	private PersonDto person;

	@Valid
	private AddressDto address;

}
