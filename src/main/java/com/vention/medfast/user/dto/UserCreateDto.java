package com.vention.medfast.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Represents the user details to be created")
public record UserCreateDto(

		@Schema(description = "User's first name", example = "John") @NotBlank(
				message = "First name is required") @Size(min = 2, max = 50) String firstName,

		@Schema(description = "User's last name",
				example = "Doe") @NotBlank(message = "Last name is required") @Size(min = 2, max = 50) String lastName,

		@Schema(description = "User's email", example = "john.doe@example.com") @NotBlank(
				message = "Email is required") @Email(message = "Invalid email format") String email,

		@Schema(description = "User's raw password", example = "StrongPass123!") @NotBlank(
				message = "Password is required") @Size(min = 8,
						message = "Password must be at least 8 characters long") String password,

		@Schema(description = "URL to profile picture (optional)",
				example = "https://example.com/photo.jpg") String profileUrl) {
}
