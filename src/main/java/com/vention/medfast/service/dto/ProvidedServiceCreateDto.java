package com.vention.medfast.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "DTO for creating and updating a service")
public record ProvidedServiceCreateDto(
		@NotNull(message = "Name is required") @Size(min = 5, max = 255,
				message = "Name should be between 5 and 255 characters") String name,

		@NotNull(message = "Description is required") @Size(min = 10, max = 255,
				message = "Description should be between 10 and 255 characters") String description,

		@NotNull(message = "Price is required") @Positive(message = "Price must be greater than 0") Double price,

		@NotNull(message = "Duration is required") @Min(value = 1,
				message = "Duration must be at least 1 minute") Integer duration,

		@NotEmpty(message = "At least one specialization is required") List<@NotNull(
				message = "Specialization id can not be null") Integer> specializationsIds) {
}
