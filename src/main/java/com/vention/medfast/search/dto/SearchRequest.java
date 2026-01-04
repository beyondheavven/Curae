package com.vention.medfast.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "DTO for search request from user")
public record SearchRequest(

		@Schema(description = "Doctor's name", example = "John") @Size(min = 2, max = 50,
				message = "Name should be between 2 and 255 characters") String doctorName,

		@Schema(description = "Doctor's surname", example = "House") @Size(min = 2, max = 50,
				message = "Doctor surname must be between 2 and 50 characters") String doctorSurname,

		@Schema(description = "Medical specialization", example = "Cardiologist") @Size(min = 2, max = 50,
				message = "Specialization must be between 2 and 100 characters") String specialization,

		@Schema(description = "Name of the service", example = "MRI Scan") @Size(min = 2, max = 100,
				message = "Service name must be between 2 and 100 characters") String serviceName) {
}
