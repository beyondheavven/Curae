package com.vention.medfast.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO for creating a visit")
public record VisitCreateDto(

		@Schema(description = "Diagnosis (optional)") String diagnosis,

		@Schema(description = "Symptoms (optional)") String symptoms,

		@Schema(description = "Medical report (optional)") String medicalReport,

		@NotNull(message = "Doctor id is required") Long doctorId,

		@NotNull(message = "Service id is required") Long serviceId,

		@NotNull(message = "Clinic id is required") Long clinicId

) {
}
