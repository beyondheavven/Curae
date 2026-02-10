package com.vention.medfast.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
@Schema(description = "Statistics for a specific doctor")
public record DoctorStatisticsResponseDto(
		@Schema(description = "Doctor ID these stats belong to", example = "42") Long doctorId,
		@Schema(description = "Date range used for calculations") Period period,
		@Schema(description = "Report generation timestamp", example = "2026-01-23T13:00:51") LocalDateTime timestamp,
		@Schema(description = "Visit appointments statistics for the doctor") VisitStatisticsDto visits,
		@Schema(description = "Online consultation statistics for the doctor") OnlineConsultationStatisticsDto onlineConsultations) {
}
