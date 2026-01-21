package com.vention.medfast.visit.dto;

import com.vention.medfast.appointment.model.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "DTO for returning visit details")
public record VisitResponseDto(DoctorForVisitResponseDto doctor, AddressForVisitResponseDto address, String clinic,
		LocalDate date, LocalTime time, AppointmentStatus status) {
}
