package com.vention.medfast.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
@Schema(description = "Clinic dashboard statistics")
public record ClinicStatisticsResponseDto(@Schema(description = "Clinic ID") Long clinicId,
		@Schema(description = "The date range used for these statistics") Period period,
		@Schema(description = "Timestamp when this report was generated") LocalDateTime timestamp,

		@Schema(description = "Visit appointments statistics") VisitStatisticsDto visits,
		@Schema(description = "Online consultation statistics") OnlineConsultationStatisticsDto onlineConsultations,
		@Schema(description = "Lab tests and results statistics") LabStatisticsDto lab) {
}
