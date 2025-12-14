package com.vention.medfast.specialization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Represents the returned doctor specialization details")
public record SpecializationResponseDto(

		@Schema(description = "Unique identifier of the specialization", example = "10") Long id,

		@Schema(description = "Name of the specialization", example = "Cardiology") String name,

		@Schema(description = "Description of the specialization",
				example = "Branch of medicine that deals with disorders of the heart.") String description) {
}
