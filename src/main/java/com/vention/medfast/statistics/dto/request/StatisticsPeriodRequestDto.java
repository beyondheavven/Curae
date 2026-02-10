package com.vention.medfast.statistics.dto.request;

import com.vention.medfast.api.validation.annotation.ValidDataRange;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Schema(description = "Query parameters for statistics by date range")
@ValidDataRange
public record StatisticsPeriodRequestDto(
		@Schema(description = "Start date of the period (yyyy-mm-dd)", example = "2026-01-01") @NotNull(
				message = "Start date is required") @PastOrPresent(
						message = "Start date cannot be in the future") LocalDate startDate,
		@Schema(description = "End date of the period (yyyy-mm-dd)",
				example = "2026-01-15") @NotNull(message = "End date is required") LocalDate endDate) {
}
