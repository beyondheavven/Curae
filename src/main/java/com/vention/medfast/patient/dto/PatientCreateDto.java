package com.vention.medfast.patient.dto;

import com.vention.medfast.address.dto.AddressCreateDto;
import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.user.dto.UserCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "DTO for creating ang getting patient")
public record PatientCreateDto(

		@Schema(description = "User account information") @NotNull(
				message = "User information is required") @Valid UserCreateDto user,

		@Schema(description = "Patient's permanent address") @NotNull(
				message = "Address is required") @Valid AddressCreateDto address,

		@Schema(description = "Citizenship of the patient", example = "USA") @NotBlank(
				message = "Citizenship is required") @Size(min = 2, max = 50,
						message = "Name of citizenship should be between 2 and 50 characters") String citizenship,

		@Schema(description = "Gender of the patient",
				example = "NOT_SPECIFIED") @NotNull(message = "Citizenship is required") Gender gender) {
}
