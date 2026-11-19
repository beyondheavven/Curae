package com.vention.curae.clinic.dto;

import com.vention.curae.address.dto.ClinicAddressRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO for creating and updating a clinic")
public record ClinicRequestDto(
		@NotBlank(message = "Name is required") @Size(max = 255,
				message = "Name must be at most 255 characters") String name,
		@Size(max = 255, message = "Logo URL must be at most 255 characters") String logoUrl, String description,
		@Valid @NotNull(message = "Address is required") ClinicAddressRequestDto address) {
}
