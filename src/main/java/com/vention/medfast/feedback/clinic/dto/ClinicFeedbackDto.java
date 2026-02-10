package com.vention.medfast.feedback.clinic.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO for listing history of feedbacks")
public record ClinicFeedbackDto(long patientId, long clinicId, int rating, String summary, LocalDateTime createdAt) {
}
