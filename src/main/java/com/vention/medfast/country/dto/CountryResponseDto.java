package com.vention.medfast.country.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Details of a country including citizenship and zip code rules")
public record CountryResponseDto(

		@Schema(description = "Unique database identifier", example = "101") Long id,

		@Schema(description = "ISO 3166-1 alpha-2 country code", example = "US") String isoCode,

		@Schema(description = "Full name of the country", example = "United States") String name,

		@Schema(description = "Name of the citizenship", example = "American") String citizenship,

		@Schema(description = "Regex pattern to validate postal codes", example = "^\\d{5}(-\\d{4})?$") String zipRegex,

		@Schema(description = "Example of a valid postal code", example = "12345") String zipExample) {
}
