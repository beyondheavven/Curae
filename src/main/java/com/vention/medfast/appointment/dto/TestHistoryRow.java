package com.vention.medfast.appointment.dto;

import com.vention.medfast.appointment.model.AppointmentStatus;

import java.time.LocalDate;

public record TestHistoryRow(Long testId, LocalDate date, AppointmentStatus status) {
}
