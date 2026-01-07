package com.vention.medfast.doctor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record DoctorSearchResponseDto(

		@Schema(description = "First name from User profile", example = "Gregory") String firstName,

		@Schema(description = "Last name from User profile", example = "House") String lastName,

		@Schema(description = "Profile picture URL", example = "https://example/avatars/house.jpg") String profileUrl,

		@Schema(description = "Doctors specialization", example = "Cardiologist") String specialization

) {
}
