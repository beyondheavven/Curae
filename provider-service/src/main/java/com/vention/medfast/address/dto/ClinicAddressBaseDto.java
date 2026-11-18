package com.vention.medfast.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for listing clinic's address for a given service")
public record ClinicAddressBaseDto(String street, String city, String zipCode) {

}