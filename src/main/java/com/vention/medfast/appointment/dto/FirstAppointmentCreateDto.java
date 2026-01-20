package com.vention.medfast.appointment.dto;

import com.vention.medfast.appointment.model.AppointmentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;

public record FirstAppointmentCreateDto(
    @NotNull AppointmentType type,

    @NotNull @Future LocalDate date,

    @NotNull LocalTime time,

    @NotNull @Positive Long doctorId
) {
}
