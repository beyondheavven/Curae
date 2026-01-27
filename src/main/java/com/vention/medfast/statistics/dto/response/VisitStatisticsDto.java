package com.vention.medfast.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statistics specifically for Physical Visits")
public record VisitStatisticsDto(@Schema(description = "Status counts for visits") AppointmentStatusCounts stats) {
}
