package com.vention.medfast.doctor.dto;

import com.vention.medfast.api.validation.annotation.ExistingClinics;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
@Schema(description = "DTO for assigning existing clinics to doctor")
public record DoctorClinicAssignmentRequestDto(@NotEmpty @ExistingClinics Set<@NotNull Long> clinicIds) {
}
