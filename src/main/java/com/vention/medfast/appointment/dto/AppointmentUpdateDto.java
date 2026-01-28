package com.vention.medfast.appointment.dto;

import com.vention.medfast.appointment.model.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "DTO for updating appointment")
public record AppointmentUpdateDto(
		@Schema(description = "Scheduled date") @NotNull(message = "Scheduled date is required") @Future(
				message = "Scheduled date must be in the future") LocalDate date,

		@Schema(description = "Scheduled time") @NotNull(message = "Scheduled time is required") LocalTime time,

		@NotNull(message = "Status is required") AppointmentStatus status

        ){
}
