package com.vention.medfast.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statistics specifically for Global Appointments")
public record AppointmentStatisticsDto(
		@Schema(description = "Status counts for all appointments") AppointmentStatusCounts stats) {
}
