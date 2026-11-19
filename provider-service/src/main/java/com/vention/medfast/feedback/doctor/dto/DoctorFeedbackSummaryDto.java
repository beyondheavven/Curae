package com.vention.medfast.feedback.doctor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

@Schema(description = "DTO for listing feedback's summary")
public record DoctorFeedbackSummaryDto(double avgRating, Page<DoctorFeedbackDto> history) {
}