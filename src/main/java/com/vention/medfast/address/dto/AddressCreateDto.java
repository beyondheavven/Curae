package com.vention.medfast.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
@Schema(description = "Represents the address details to be created")
public record AddressCreateDto(

		@Schema(description = "Street name",
				example = "Baker Street") @NotBlank(message = "Street is required") String street,

		@Schema(description = "City name", example = "London") @NotBlank(message = "City is required") String city,

		@Schema(description = "House number",
				example = "221B") @NotBlank(message = "House number is required") String houseNumber,

		@Schema(description = "State or region",
				example = "Greater London") @NotBlank(message = "State is required") String state,

		@Schema(description = "Apartment number",
				example = "4A") @NotBlank(message = "Apartment number is required") String apartment,

		@Schema(description = "ZIP or Postal code",
				example = "NW1 6XE") @NotBlank(message = "Zip code is required") String zipCode,

		@Schema(description = "Phone number associated with address", example = "+1234567890") @NotBlank(
				message = "Phone number is required") @Pattern(regexp = "^\\+?[0-9]{10,15}$",
						message = "Invalid phone number format") String phone) {
}
