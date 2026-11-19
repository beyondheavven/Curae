package com.vention.curae.appointment.dto;

import com.vention.curae.appointment.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateOnlineConsultationRequest(@NotNull Long patientId, @NotNull Long doctorId, @NotNull LocalDate date,
		@NotNull LocalTime time, AppointmentStatus status, String meetingLink) {
}
