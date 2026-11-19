package com.vention.curae.feedback.doctor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "DTO for listing history of feedbacks")
public record DoctorFeedbackDto(long patientId, long doctorId, int rating, String summary, LocalDateTime createdAt) {
}