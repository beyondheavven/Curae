package com.vention.medfast.appointment.dto;

import com.vention.medfast.appointment.model.AppointmentStatus;
import jakarta.validation.constraints.Future;
import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentUpdateDto(@Future LocalDate date, LocalTime time, AppointmentStatus status) {
}
