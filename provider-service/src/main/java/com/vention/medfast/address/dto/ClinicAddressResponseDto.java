package com.vention.medfast.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents clinic address details")
public record ClinicAddressResponseDto(Long id, String street, String city, String houseNumber, String state,
		String apartment, String zipCode, String phone) {
}
