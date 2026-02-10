package com.vention.medfast.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statistics for Lab (tests + results)")
public record LabStatisticsDto(
		@Schema(description = "Status counts for lab test appointments") AppointmentStatusCounts tests,
		@Schema(description = "Status counts for lab results processing") LabResultStatusCounts results) {
}
