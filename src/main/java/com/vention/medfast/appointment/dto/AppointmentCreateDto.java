package com.vention.medfast.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "DTO for creating appointment")
public record AppointmentCreateDto(

		@Schema(description = "Referral id issued by doctor") @Positive(
				message = "Referral id must be positive") Long referralId,

		@Schema(description = "Scheduled date") @NotNull(message = "Scheduled date is required") @Future(
				message = "Scheduled date must be in the future") LocalDate date,

		@Schema(description = "Scheduled time") @NotNull(message = "Scheduled time is required") LocalTime time) {
}
