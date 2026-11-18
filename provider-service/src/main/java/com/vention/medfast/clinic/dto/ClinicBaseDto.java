package com.vention.medfast.clinic.dto;

import com.vention.medfast.address.dto.ClinicAddressBaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "DTO for listing available clinics for a given service")
public record ClinicBaseDto(Long id, String name, String description, ClinicAddressBaseDto address) {
}