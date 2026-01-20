package com.vention.medfast.visit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO for creating a visit")
public record VisitCreateDto(

		@NotNull(message = "Appointment id is required") Long appointmentId,

		@NotBlank(message = "Diagnosis is required") String diagnosis,

		@NotBlank(message = "Symptoms are required") String symptoms,

		@NotBlank(message = "Medical report is required") String medicalReport

) {
}
