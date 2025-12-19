package com.vention.medfast.registration.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegistrationRequestDto {

	@Valid
	private PersonDto person;

	@Valid
	private AddressDto address;

}
