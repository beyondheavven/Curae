package com.vention.medfast.service.dto;

import com.vention.medfast.specialization.dto.SpecializationBaseDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Represents the provided service details")
public record ProvidedServiceResponseDto(Long id, String name, String description, Double price, Integer duration,
		List<SpecializationBaseDto> specializations) {
}
