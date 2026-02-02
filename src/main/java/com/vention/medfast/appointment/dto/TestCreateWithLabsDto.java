package com.vention.medfast.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record TestCreateWithLabsDto(@NotNull Long patientId, @NotNull Long clinicId,
		@NotNull @FutureOrPresent @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@NotNull @JsonProperty("time") @JsonFormat(pattern = "HH:mm:ss") LocalTime time,
		@NotEmpty List<Long> labTestIds) {
}
