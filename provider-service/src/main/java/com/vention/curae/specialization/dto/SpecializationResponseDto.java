package com.vention.curae.specialization.dto;

import com.vention.curae.service.dto.ProvidedServiceBaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Represents the returned doctor specialization details")
public record SpecializationResponseDto(Long id, String name, String description,
		List<ProvidedServiceBaseDto> services) {
}