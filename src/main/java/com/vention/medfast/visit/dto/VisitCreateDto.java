package com.vention.medfast.visit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "DTO for creating a visit")
public record VisitCreateDto(
		@NotNull(message = "Scheduled date is required") @Future(
				message = "Scheduled date must be in the future") LocalDate scheduledDate,

		@NotNull(message = "Scheduled time is required") LocalTime scheduledTime,

		@NotNull(message = "Doctor id is required") @Positive(message = "Doctor id must be positive") Long doctorId,

		@NotNull(message = "Patient id is required") @Positive(message = "Patient id must be positive") Long patientId,

		@NotNull(message = "Patient id is required") @Positive(
				message = "Patient id must be positive") Long serviceId) {
}
