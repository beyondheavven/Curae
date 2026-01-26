package com.vention.medfast.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Aggregated platform statistics for the Admin dashboard")
public record StatisticsResponseDto(@Schema(description = "The date range used for these statistics") Period period,

		@Schema(description = "Timestamp when this report was generated",
				example = "2026-01-23T13:00:51") LocalDateTime timestamp,

		@Schema(description = "Global appointment statistics (Visits + Tests)") AppointmentStatisticsDto appointments,

		@Schema(description = "Statistics for doctor visits only") VisitStatisticsDto visits,

		@Schema(description = "Statistics for lab tests and result processing") LabTestStatisticsDto labResults) {
}
