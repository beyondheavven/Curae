package com.vention.medfast.referral.dto;

import com.vention.medfast.appointment.model.AppointmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Schema(description = "DTO for creating referral")
public record ReferralCreateDto(

		@Schema(description = "Patient id") @NotNull(message = "Patient id is required") @Positive(
				message = "Patient id must be positive") Long patientId,

		@Schema(description = "Service id") @NotNull(message = "Service id is required") @Positive(
				message = "Service id must be positive") Long serviceId,

		@Schema(description = "Appointment type") @NotNull AppointmentType appointmentType,

		@Schema(description = "Expiration date of referral") @NotNull(
				message = "Expiration date is required") @Future(
						message = "Expiration date must be in the future") LocalDate expiresAt) {
}
