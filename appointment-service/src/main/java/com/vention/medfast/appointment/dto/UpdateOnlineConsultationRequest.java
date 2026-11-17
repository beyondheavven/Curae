package com.vention.medfast.appointment.dto;

import com.vention.medfast.appointment.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateOnlineConsultationRequest(@NotNull Long onlineConsultationId, @NotNull Long patientId,
		@NotNull Long doctorId, @NotNull LocalDate date, @NotNull LocalTime time, AppointmentStatus status,
		String meetingLink) {
}
