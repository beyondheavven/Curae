package com.vention.medfast.globalsearch.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "DTO for search request from user")
public record SearchRequest(

		@Schema(description = "Search keyword for doctors (name, surname), specializations, or services",
				example = "Gregory") @Size(min = 2, max = 100,
						message = "Search query must be between 2 and 100 characters") String query) {

}
