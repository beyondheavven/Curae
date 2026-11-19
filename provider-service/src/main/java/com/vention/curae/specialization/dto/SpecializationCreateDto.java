package com.vention.curae.specialization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "DTO for creating and updating a specialization")
public record SpecializationCreateDto(
		@NotNull(message = "Name is required") @Size(min = 3, max = 50,
				message = "Name should be between 3 and 50 characters") String name,
		@NotNull(message = "Description is required") @Size(min = 10, max = 255,
				message = "Description should be between 10 and 255 characters") String description,
		@NotEmpty(message = "At least one service is required") List<@NotNull(
				message = "Service id can not be null") Integer> serviceIds) {
}