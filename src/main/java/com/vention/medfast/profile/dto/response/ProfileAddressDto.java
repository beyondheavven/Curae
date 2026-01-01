package com.vention.medfast.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileAddressDto {

	private String state;

	private String city;

	private String street;

	private String zipCode;

	private String houseNumber;

	private String apartment;

}
