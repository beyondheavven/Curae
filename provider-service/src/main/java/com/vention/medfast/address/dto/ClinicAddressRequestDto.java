package com.vention.medfast.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO for creating and updating clinic address")
public record ClinicAddressRequestDto(
		@NotBlank(message = "Street is required") String street,
		@NotBlank(message = "City is required") String city,
		@NotBlank(message = "House number is required") String houseNumber,
		@NotBlank(message = "State is required") String state,
		@NotBlank(message = "Apartment number is required") String apartment,
		@NotBlank(message = "Zip code is required") String zipCode,
		String phone) {
}
