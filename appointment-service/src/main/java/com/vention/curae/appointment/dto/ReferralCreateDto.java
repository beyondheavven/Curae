package com.vention.curae.appointment.dto;

import com.vention.curae.appointment.model.AppointmentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReferralCreateDto(@NotNull Long patientId, @NotNull Long serviceId,
		@NotNull AppointmentType appointmentType, @Future LocalDate expiresAt) {
}
