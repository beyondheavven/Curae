package com.vention.medfast.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Represents the returned patient details")
public record PatientResponseDto(

		@Schema(description = "Unique identifier of the patient", example = "10") Long id,

		@Schema(description = "First name from User account", example = "John") String firstName,

		@Schema(description = "Last name from User account", example = "Doe") String lastName,

		@Schema(description = "Profile picture URL. Returns default icon if null.",
				example = "https://www.figma.com/design/KKg1Dqv2sRotJhobb1cyl4/Medfast?node-id=2112-67764&t=1kBppRETzZPTN2fi-4") String profileUrl

) {
}
