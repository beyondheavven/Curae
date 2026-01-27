package com.vention.medfast.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed breakdown of appointment statuses")
public record AppointmentStatusCounts(@Schema(description = "Total number of appointments", example = "50") long total,
		@Schema(description = "Number of scheduled appointments", example = "10") long scheduled,
		@Schema(description = "Number of cancelled appointments", example = "5") long cancelled,
		@Schema(description = "Number of successfully completed appointments", example = "30") long completed,
		@Schema(description = "Number of missed appointments", example = "5") long missed) {
}
