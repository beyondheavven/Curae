package com.vention.medfast.specialization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "DTO for creating and updating a specialization")
public record SpecializationCreateDto(

		@Schema(description = "Name of the specialization", example = "Cardiology", minLength = 3,
				maxLength = 50) @NotNull(message = "Name is required") @Size(min = 3, max = 50,
						message = "Name should be between 3 and 50 characters") String name,

		@Schema(description = "Detailed description of the specialization",
				example = "Branch of medicine that deals with disorders of the heart.", minLength = 10,
				maxLength = 255) @NotNull(message = "Description is required") @Size(min = 10, max = 255,
						message = "Description should be between 10 and 255 characters") String description) {
}
