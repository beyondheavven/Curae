package com.vention.medfast.appointment.dto;

import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record OnlineConsultationDetails(Long id, Long patientId, Long doctorId, LocalDate date, LocalTime time,
		AppointmentStatus status, AppointmentType type, Instant updatedAt, String meetingLink) {
}
