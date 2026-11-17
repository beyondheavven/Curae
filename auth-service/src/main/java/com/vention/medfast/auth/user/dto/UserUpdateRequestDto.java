package com.vention.medfast.auth.user.dto;

import com.vention.medfast.user.model.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateRequestDto(
		@NotBlank(message = "Email is required") @Email(message = "Must be a valid email format") String email,
		@Size(min = 8, message = "Password minimum required length 8") String password,
		@NotBlank(message = "First name cannot be empty") @Size(max = 50) String firstName,
		@NotBlank(message = "Last name cannot be empty") @Size(max = 50) String lastName,
		@NotNull(message = "Birth date is required") @Past(
				message = "Birth date must be in the past") LocalDate birthDate,
		@NotNull(message = "Status is required") Status status, String profileUrl) {
}
