package com.vention.curae.doctor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

@Schema(description = "DTO for assigning existing clinics to doctor")
public record DoctorClinicAssignmentRequestDto(@NotEmpty Set<@NotNull Long> clinicIds) {
}