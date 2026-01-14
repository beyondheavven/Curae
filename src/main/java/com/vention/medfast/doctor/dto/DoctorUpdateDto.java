package com.vention.medfast.doctor.dto;

import com.vention.medfast.api.validation.annotation.ExistingClinics;
import com.vention.medfast.api.validation.annotation.ExistingSpecializations;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder
@Schema(description = "DTO to update doctor")
public record DoctorUpdateDto(@NotBlank(message = "First name is required") @Size(min = 2, max = 50) String firstName,
		@NotBlank(message = "Last name is required") @Size(min = 2, max = 50) String lastName,
		@NotBlank(message = "Bio is required") @Size(min = 5, max = 255) String bio,
		@NotBlank(message = "Phone number is required") String phoneNumber,
		@NotBlank(message = "License number is required") String licenseNumber,
		@NotEmpty(
				message = "Select at least one existing specialization") @ExistingSpecializations Set<@NotNull Long> specializationIds,
		@NotEmpty(message = "Select at least one existing clinic") @ExistingClinics Set<@NotNull Long> clinicIds) {
}
