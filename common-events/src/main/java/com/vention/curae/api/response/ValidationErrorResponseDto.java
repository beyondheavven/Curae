package com.vention.curae.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@SuperBuilder
@Schema(name = "ValidationErrorResponseDto", description = "Error response returned validation fails")
public class ValidationErrorResponseDto extends ErrorResponseDto {

	@Schema(description = "Map of field-specific validation errors where key = field name and value = error message",
			example = "{\"name\": \"must not be blank\", \"description\": \"must be at least 10 characters\"}")
	private Map<String, String> fieldErrors;

}
