package com.vention.medfast.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statistics for Online Consultations")
public record OnlineConsultationStatisticsDto(
		@Schema(description = "Status counts for online consultations") AppointmentStatusCounts stats) {
}
