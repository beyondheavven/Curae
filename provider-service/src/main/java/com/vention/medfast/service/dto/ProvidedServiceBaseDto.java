package com.vention.medfast.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for listing available services for a given specialization")
public record ProvidedServiceBaseDto(Long id, String name, String description, Double price, Integer duration) {
}