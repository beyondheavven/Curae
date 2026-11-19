package com.vention.curae.specialization.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for listing available specializations for a given service")
public record SpecializationBaseDto(Long id, String name, String description) {
}