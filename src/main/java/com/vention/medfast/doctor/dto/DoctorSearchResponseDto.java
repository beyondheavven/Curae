package com.vention.medfast.doctor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "Base doctor details")
public class DoctorSearchResponseDto {

	@Schema(description = "First name from User profile", example = "Gregory")
	private String firstName;

	@Schema(description = "Last name from User profile", example = "House")
	private String lastName;

	@Schema(description = "Profile picture URL", example = "https://example/avatars/house.jpg")
	private String profileUrl;

	@Schema(description = "Doctors specialization", example = "Cardiologist")
	private String specialization;

}
