package com.vention.curae.feedback.clinic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO for creating feedback for clinic")
public record ClinicFeedbackCreateDto(@NotNull(message = "Clinic id is required") Long clinicId,

		@NotNull(message = "Rating is required") @Min(value = 1, message = "Rating must be at least 1") @Max(value = 5,
				message = "Rating must be at most 5") Integer rating,

		@NotBlank(message = "Summary is required") @Size(max = 500,
				message = "Summary must be at most 500 characters") @Size(min = 5,
						message = "Summary must be at least 5 characters") String summary) {
}