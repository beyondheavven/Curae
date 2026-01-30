package com.vention.medfast.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for doctor details representation for a visit")
public record DoctorForVisitResponseDto(String firstName, String lastName, List<String> specializations) {
}
