package com.vention.medfast.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileAddressUpdateDto {

	@NotBlank(message = "Street is required")
	@Size(max = 100)
	private String street;

	@NotBlank(message = "City is required")
	@Size(max = 50)
	private String city;

	@Size(max = 10)
	private String apartment;

	@NotBlank(message = "House number is required")
	@Size(max = 10)
	private String houseNumber;

	@NotBlank(message = "Country is required")
	@Size(max = 50)
	private String state;

	@NotBlank(message = "Zip code is required")
	@Size(min = 3, max = 12)
	private String zipCode;

}
