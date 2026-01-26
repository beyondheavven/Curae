package com.vention.medfast.statistics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "The requested date period")
public record Period(@Schema(description = "Start of the range", example = "2026-01-01") LocalDate startDate,
		@Schema(description = "End of the range", example = "2026-01-24") LocalDate endDate) {
}
