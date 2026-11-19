package com.vention.curae.user.dto;

import com.vention.curae.user.model.RoleType;
import com.vention.curae.user.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Represents the returned user's details")
public record UserResponseDto(@Schema(description = "Unique ID of user", example = "10") Long id,
		@Schema(description = "User's registered email", example = "john.doe@example.com") String email,
		@Schema(description = "First name of User", example = "John") String firstName,
		@Schema(description = "Last name of User", example = "Doe") String lastName,
		@Schema(description = "URL to profile picture of User",
				example = "https://www.figma.com/design/KKg1Dqv2sRotJhobb1cyl4/Curae?node-id=2112-67764&t=1kBppRETzZPTN2fi-4") String profileUrl,
		@Schema(description = "Date of birth", example = "2000-01-01") LocalDate birthDate,
		@Schema(description = "User Role", example = "PATIENT") RoleType role,
		@Schema(description = "Account status", example = "ACTIVE") Status status) {
}
