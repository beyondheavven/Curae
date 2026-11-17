package com.vention.medfast.appointment.dto;

import com.vention.medfast.appointment.model.AppointmentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReferralCreateDto(@NotNull Long patientId, @NotNull Long serviceId,
		@NotNull AppointmentType appointmentType, @Future LocalDate expiresAt) {
}
