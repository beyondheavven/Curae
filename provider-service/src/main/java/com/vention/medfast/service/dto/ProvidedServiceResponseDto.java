package com.vention.medfast.service.dto;

import com.vention.medfast.specialization.dto.SpecializationBaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Represents the provided service details")
public record ProvidedServiceResponseDto(
		@Schema(description = "Service ID", example = "1") Long id,
		@Schema(description = "Service Name", example = "General Consultation") String name,
		@Schema(description = "Short description", example = "Primary health check...") String description,
		@Schema(description = "Price", example = "50.00") Double price,
		@Schema(description = "Duration in minutes", example = "30") Integer duration,
		@Schema(description = "List of related specializations") List<SpecializationBaseDto> specializations) {
}