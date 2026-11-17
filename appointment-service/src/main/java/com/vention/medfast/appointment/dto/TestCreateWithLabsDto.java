package com.vention.medfast.appointment.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record TestCreateWithLabsDto(@NotNull Long patientId, @NotNull Long clinicId, @FutureOrPresent LocalDate date,
		@NotNull LocalTime time, List<Long> labTestIds) {
}
