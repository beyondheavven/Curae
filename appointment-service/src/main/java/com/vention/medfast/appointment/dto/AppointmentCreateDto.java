package com.vention.medfast.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentCreateDto(Long referralId, @NotNull @Future LocalDate date, @NotNull LocalTime time) {
}
