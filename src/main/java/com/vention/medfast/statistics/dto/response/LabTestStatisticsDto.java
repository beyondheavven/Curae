package com.vention.medfast.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statistics for Lab Tests and their Results")
public record LabTestStatisticsDto(
		@Schema(description = "Status counts for test appointments") AppointmentStatusCounts stats,
		@Schema(description = "Status counts for the actual lab results") LabResultStatusCounts results) {
}
