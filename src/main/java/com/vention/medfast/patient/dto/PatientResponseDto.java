package com.vention.medfast.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Represents the returned patient details")
public record PatientResponseDto(

    @Schema(description = "First name from User account", example = "John")
    String firstName,

    @Schema(description = "Last name from User account", example = "Doe")
    String lastName,

    @Schema(description = "Profile picture URL. Returns default icon if null.", example = "https://medfast.com/images/default.png")
    String profileUrl

) {
}
