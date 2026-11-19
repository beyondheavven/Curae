package com.vention.curae.registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Schema(description = "DTO for doctor registration as admin")
public record DoctorRegistrationRequestDto(

		@NotBlank(message = "First name is required") @Size(min = 2, max = 50) String firstName,

		@NotBlank(message = "Last name is required") @Size(min = 2, max = 50) String lastName,

		@NotNull(message = "Date of birth is required") @Past(
				message = "Birth date must be in the past") LocalDate birthDate,

		@NotEmpty(message = "Select at least one specialization") Set<@NotNull(
				message = "Specialization id can not be null") Long> specializationIds,

		@NotBlank(message = "License number is required") String licenseNumber,

		@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

		@NotBlank(message = "Phone number is required") String phoneNumber) {
}
