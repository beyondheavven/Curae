package com.vention.curae.appointment.dto;

import com.vention.curae.appointment.model.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record VisitListItemDto(String doctorFirstName, String doctorLastName, String clinicName, String city,
		LocalDate date, LocalTime time, AppointmentStatus status) {
}
