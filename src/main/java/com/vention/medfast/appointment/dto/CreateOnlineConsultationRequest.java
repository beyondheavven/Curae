package com.vention.medfast.appointment.dto;

import com.vention.medfast.appointment.model.AppointmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateOnlineConsultationRequest(@NotNull Long patientId, @NotNull Long doctorId,
		@NotNull @FutureOrPresent LocalDate date, @NotNull LocalTime time, @NotNull AppointmentStatus status,
		@NotNull String meetingLink) {
}
