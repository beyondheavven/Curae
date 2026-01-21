package com.vention.medfast.visit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for address representation for clinic where ")
public record AddressForVisitResponseDto(String city, String street, String houseNumber) {
}
