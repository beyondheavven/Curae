package com.vention.medfast.clinic.dto;

import com.vention.medfast.address.dto.ClinicAddressResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents clinic details")
public record ClinicResponseDto(Long id, String name, String logoUrl, String description,
		ClinicAddressResponseDto address) {
}
